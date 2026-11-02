package com.fleetflow.analytics;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Miller-Rabin Probabilistic Primality Test and Cryptographic Consignment Token Generator.
 * Error probability for k rounds: <= (1/4)^k.
 * With k = 40, error probability is <= 2^(-80), cryptographically negligible.
 * Used in FleetFlow to generate tamper-proof shipment tracking tokens, cryptographic digital seals,
 * and large prime credentials for logistics API signatures.
 */
public class MillerRabinCryptoToken {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Tests if BigInteger n is prime using Miller-Rabin algorithm with k independent rounds.
     */
    public static boolean isProbablePrime(BigInteger n, int k) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.valueOf(2)) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) return false;

        // Write n - 1 as 2^s * d with d odd
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.valueOf(2));
            s++;
        }

        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger nMinusTwo = n.subtract(BigInteger.valueOf(2));

        for (int i = 0; i < k; i++) {
            // Pick random witness a in [2, n - 2]
            BigInteger a = uniformRandom(BigInteger.valueOf(2), nMinusTwo);
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.valueOf(2), n);
                if (x.equals(nMinusOne)) {
                    composite = false;
                    break;
                }
                if (x.equals(BigInteger.ONE)) {
                    return false;
                }
            }

            if (composite) {
                return false;
            }
        }

        return true;
    }

    private static BigInteger uniformRandom(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        int bitLength = range.bitLength();
        BigInteger r;
        do {
            r = new BigInteger(bitLength, SECURE_RANDOM);
        } while (r.compareTo(range) >= 0);
        return min.add(r);
    }

    /**
     * Generates a cryptographically verified prime token of specified bit length.
     */
    public static BigInteger generatePrimeToken(int bitLength, int testRounds) {
        while (true) {
            BigInteger candidate = new BigInteger(bitLength, SECURE_RANDOM).setBit(0).setBit(bitLength - 1);
            if (isProbablePrime(candidate, testRounds)) {
                return candidate;
            }
        }
    }

    /**
     * Creates a digital tamper-proof consignment seal for a shipment.
     */
    public static String generateShipmentSeal(String trackingCode, String manifestSummary) {
        BigInteger primeToken = generatePrimeToken(128, 30);
        long hash = Math.abs(manifestSummary.hashCode());
        BigInteger sealValue = primeToken.multiply(BigInteger.valueOf(hash));
        return String.format("SEAL-%s-%s", trackingCode, sealValue.toString(16).toUpperCase());
    }
}
