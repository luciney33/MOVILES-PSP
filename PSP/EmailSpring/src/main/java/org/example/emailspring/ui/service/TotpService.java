package org.example.emailspring.ui.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.error.BadRequestException;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class TotpService {

    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier verifier;

    public TotpService() {
        DefaultCodeVerifier defaultVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        defaultVerifier.setTimePeriod(30);
        defaultVerifier.setAllowedTimePeriodDiscrepancy(1);
        this.verifier = defaultVerifier;
    }


    public String generateSecret() {
        return secretGenerator.generate();
    }


    public String generateQrCodeImageUri(String secret, String username, String issuer) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        byte[] imageData = qrGenerator.generate(data);
        return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
    }


    public String generateQrCode(String secret, String username) {
        try {
            return generateQrCodeImageUri(secret, username, Constantes.NOM_APP_GOOGLEAUTHTENTICATOR);
        } catch (QrGenerationException e) {
            throw new BadRequestException(Constantes.ERROR_GENERANDO_CODIGO_QR + e.getMessage());
        }
    }


    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }


}

