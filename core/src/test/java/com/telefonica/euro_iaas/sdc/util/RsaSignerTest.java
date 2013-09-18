package com.telefonica.euro_iaas.sdc.util;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit test for RSA Signer
 *
 * @author Sergio Arroyo
 *
 */
public class RsaSignerTest {

    private final static String SIGNED =
        "H7BcEHFptR1YDm3S1cnTLYl+5lAkpU8m/PPgs4Xsexes2KiU4LYbZQ/Ib/un"
            + "LpuZ/AeoQuCw/retBmDCx4EEll6AC83Pr+3kmpq05nEMN5wWIz0zhkqLu7Kp"
            + "bTppDtjBGCDckaB5GQ0J7Upowy3BKF+ov2AUqNI2gRIISGuQtEeuSHzE1wP3"
            + "Qt2jeL1Xo8sdqEUIBcgWG200iPTQGKsqlk3KSIL89rdgbEPkm/mL+nbVYWWL"
            + "rLjPz4TcqB/pwZuwx+7g29dGfQKcxTEOTR3ilqpn8Dl7mB/t0BlNiTLogwGD"
            + "DW9v+X5Wf03fvM3gFTraw///jpfAw10B0HP6wipgEw==";

    private final static String BODY =
        "Method:GET\n"
        + "Hashed Path:bEM+o9tCkqf41JkAi8jgkkr5ZvU=\n"
        + "X-Ops-Content-Hash:2jmj7l5rSw0yVb/vlWAYkK/YBwk=\n"
        + "X-Ops-Timestamp:2011-07-14T10:10:55Z\n"
        + "X-Ops-UserId:serch";

    @Test
    public void testSign() throws Exception {
        Assert.assertEquals(SIGNED, new RSASignerImpl().sign(BODY,
                new File(this.getClass().getResource("/private.pem").toURI())));
    }

    @Test
    public void testUnsign() throws Exception {
        Assert.assertEquals(BODY, new RSASignerImpl().unsign(SIGNED,
                new File(this.getClass().getResource("/public.pem").toURI())));
    }

}
