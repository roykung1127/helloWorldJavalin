package app.utils;

//import org.bouncycastle.util.io.pem.PemObject;
//import org.bouncycastle.util.io.pem.PemReader;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.security.KeyFactory;
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//import java.security.spec.EncodedKeySpec;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;

public class PemUtils {

//    public static byte[] parsePEMFile(File pemFile) throws IOException {
//    	Log.log("File Path:"+pemFile.getAbsolutePath(), LogType.INFO);
//        if (!pemFile.isFile() || !pemFile.exists()) {
//            throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
//        }
//        PemReader reader = new PemReader(new FileReader(pemFile));
//        PemObject pemObject = reader.readPemObject();
//        byte[] content = pemObject.getContent();
//        reader.close();
//        return content;
//    }
//
//    public static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
//        PublicKey publicKey = null;
//        try {
//            KeyFactory kf = KeyFactory.getInstance(algorithm);
//            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//            publicKey = kf.generatePublic(keySpec);
//        } catch (NoSuchAlgorithmException e) {
//        	Log.log("Could not reconstruct the public key, the given algorithm could not be found.", LogType.ERROR, e);
//        } catch (InvalidKeySpecException e) {
//        	Log.log("Could not reconstruct the public key", LogType.ERROR, e);
//        }
//
//        return publicKey;
//    }
//
//    public static PublicKey getpublicKey(byte[] keyBytes, String algorithm) {
//        PublicKey publicKey = null;
//        try {
//            KeyFactory kf = KeyFactory.getInstance(algorithm);
//            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//            publicKey = kf.generatePublic(keySpec);
//        } catch (NoSuchAlgorithmException e) {
//        	Log.log("Could not reconstruct the public key, the given algorithm could not be found.", LogType.ERROR, e);
//        } catch (InvalidKeySpecException e) {
//        	Log.log("Could not reconstruct the public key", LogType.ERROR, e);
//        }
//
//        return publicKey;
//    }
//
//    public static PublicKey readPublicKeyFromFile(String filepath, String algorithm) throws IOException {
//        byte[] bytes = PemUtils.parsePEMFile(new File(filepath));
//
//        Log.info(""+bytes);
//        return PemUtils.getPublicKey(bytes, algorithm);
//    }
//
//    public static PublicKey readpublicKeyFromFile(String filepath, String algorithm) throws IOException {
//        byte[] bytes = PemUtils.parsePEMFile(new File(filepath));
//
//        Log.info(""+bytes);
//        return PemUtils.getpublicKey(bytes, algorithm);
//    }

}
