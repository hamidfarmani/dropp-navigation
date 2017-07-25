package util;

import java.util.Random;
import java.util.UUID;

/**
 * Created by kasra on 2/5/2017.
 */
public class Generator {

    Random r = new Random();
    public String generateToken(int numberOfDigits){
        if(numberOfDigits>100){
            throw new IllegalArgumentException();
        }
        String token="";
        for (int i = 0; i < numberOfDigits; i++) {
            token+=r.nextInt(10);
        }
        return token;
    }

    public String generateRandomCode(int num){
        String code = "";
        int rand=0;
        for(int i=0;i<num;i++){
            while(rand<48 || (rand>57 && rand<65) || (rand>90 && rand<97) || rand>122){
                rand = (int)(Math.random() * 122 + 48);
            }
            code = code + String.valueOf((char) rand);
            rand = 0;
        }
        return code;
    }

    public String generateUUID(){
        return UUID.randomUUID().toString();
    }

}
