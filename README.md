# Sky Shipping Estimator

A focused Scala 3 engineering product for deterministic parcel-shipping cost estimates.

## Status

**Engineering beta.** The estimator has a real Scala implementation, input validation, deterministic money rounding, unit tests, and GitHub Actions compile/test gates. It is not a carrier-rate integration, tax engine, address validator, fulfillment platform, or production pricing authority.

## Model

The current quote model uses:

- base charge: `4.50`
- distance charge: `0.035` per kilometer
- weight charge: `0.80` per kilogram
- service multiplier: Economy `1.00`, Standard `1.25`, Express `1.75`

Currency is intentionally unspecified. Callers must define currency and commercial policy outside this library.

## Build and test

Requirements: Java 21 and sbt.

```bash
sbt -batch clean compile
sbt -batch test
```

## CLI example

```bash
sbt 'runMain com.skycoin4444.shipping.ShippingEstimatorCli 10 100 standard'
```

Expected output:

```text
shipping_total=20.00
```

Invalid weights, negative distances, unknown service levels, and non-numeric CLI values fail explicitly.

## Architecture

`ShippingEstimator` is a pure calculation boundary returning `Either[String, ShippingQuote]`. It has no network calls, persistence, secrets, or external carrier dependency. `ShippingEstimatorCli` is a thin command-line adapter over the library.

This repository previously contained a generic Python anomaly endpoint and npm metadata that did not match the repository name. The productization branch replaces that mismatch with an actual Scala shipping estimator while preserving the original Git history.

## SKYCOIN4444 integration

The library can be wrapped behind a stable API by marketplace, checkout, or fulfillment modules. Production integration should supply currency rules, carrier/service mapping, taxes, insurance, dimensional-weight policy, destination zones, rate versioning, and auditability.

## Security and operational boundaries

The current library does not process credentials or customer data. It does not validate postal addresses or protect callers from commercially invalid pricing assumptions. Treat the built-in coefficients as demonstrative defaults, not real carrier prices.

## License

See `LICENSE`.
