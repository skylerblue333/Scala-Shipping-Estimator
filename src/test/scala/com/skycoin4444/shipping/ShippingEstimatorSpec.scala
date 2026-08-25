package com.skycoin4444.shipping

class ShippingEstimatorSpec extends munit.FunSuite:
  test("standard quote is deterministic") {
    val result = ShippingEstimator.estimate(
      ShippingRequest(BigDecimal("10"), BigDecimal("100"), ServiceLevel.Standard)
    )
    val quote = result.toOption.get
    assertEquals(quote.baseCharge, BigDecimal("4.50"))
    assertEquals(quote.distanceCharge, BigDecimal("3.50"))
    assertEquals(quote.weightCharge, BigDecimal("8.00"))
    assertEquals(quote.total, BigDecimal("20.00"))
  }

  test("express costs more than economy for the same shipment") {
    val economy = ShippingEstimator
      .estimate(ShippingRequest(BigDecimal("2"), BigDecimal("50"), ServiceLevel.Economy))
      .toOption.get
    val express = ShippingEstimator
      .estimate(ShippingRequest(BigDecimal("2"), BigDecimal("50"), ServiceLevel.Express))
      .toOption.get
    assert(express.total > economy.total)
  }

  test("rejects non-positive weight") {
    val result = ShippingEstimator.estimate(
      ShippingRequest(BigDecimal("0"), BigDecimal("10"), ServiceLevel.Standard)
    )
    assertEquals(result, Left("weightKg must be greater than zero"))
  }

  test("rejects negative distance") {
    val result = ShippingEstimator.estimate(
      ShippingRequest(BigDecimal("1"), BigDecimal("-1"), ServiceLevel.Standard)
    )
    assertEquals(result, Left("distanceKm must be zero or greater"))
  }
