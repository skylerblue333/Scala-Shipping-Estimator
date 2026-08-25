package com.skycoin4444.shipping

import scala.math.BigDecimal.RoundingMode

enum ServiceLevel(val multiplier: BigDecimal):
  case Economy extends ServiceLevel(BigDecimal("1.00"))
  case Standard extends ServiceLevel(BigDecimal("1.25"))
  case Express extends ServiceLevel(BigDecimal("1.75"))

final case class ShippingRequest(
    weightKg: BigDecimal,
    distanceKm: BigDecimal,
    serviceLevel: ServiceLevel
)

final case class ShippingQuote(
    baseCharge: BigDecimal,
    distanceCharge: BigDecimal,
    weightCharge: BigDecimal,
    serviceMultiplier: BigDecimal,
    total: BigDecimal
)

object ShippingEstimator:
  private val BaseCharge = BigDecimal("4.50")
  private val PerKm = BigDecimal("0.035")
  private val PerKg = BigDecimal("0.80")

  def estimate(request: ShippingRequest): Either[String, ShippingQuote] =
    if request.weightKg <= 0 then Left("weightKg must be greater than zero")
    else if request.distanceKm < 0 then Left("distanceKm must be zero or greater")
    else
      val distanceCharge = money(request.distanceKm * PerKm)
      val weightCharge = money(request.weightKg * PerKg)
      val subtotal = BaseCharge + distanceCharge + weightCharge
      val total = money(subtotal * request.serviceLevel.multiplier)
      Right(
        ShippingQuote(
          baseCharge = BaseCharge,
          distanceCharge = distanceCharge,
          weightCharge = weightCharge,
          serviceMultiplier = request.serviceLevel.multiplier,
          total = total
        )
      )

  private def money(value: BigDecimal): BigDecimal =
    value.setScale(2, RoundingMode.HALF_UP)

object ShippingEstimatorCli:
  def main(args: Array[String]): Unit =
    if args.length != 3 then
      Console.err.println("usage: <weightKg> <distanceKm> <economy|standard|express>")
      sys.exit(2)

    val result = for
      weight <- parseDecimal(args(0), "weightKg")
      distance <- parseDecimal(args(1), "distanceKm")
      service <- parseService(args(2))
      quote <- ShippingEstimator.estimate(ShippingRequest(weight, distance, service))
    yield quote

    result match
      case Left(error) =>
        Console.err.println(error)
        sys.exit(2)
      case Right(quote) =>
        println(s"shipping_total=${quote.total}")

  private def parseDecimal(raw: String, field: String): Either[String, BigDecimal] =
    raw.toDoubleOption match
      case Some(value) if value.isFinite => Right(BigDecimal(raw))
      case _ => Left(s"$field must be a finite decimal number")

  private def parseService(raw: String): Either[String, ServiceLevel] =
    raw.trim.toLowerCase match
      case "economy" => Right(ServiceLevel.Economy)
      case "standard" => Right(ServiceLevel.Standard)
      case "express" => Right(ServiceLevel.Express)
      case _ => Left("service level must be economy, standard, or express")
