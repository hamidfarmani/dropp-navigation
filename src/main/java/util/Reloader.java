package util;

import model.enums.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Created by Hamid on 7/15/2017.
 */
public class Reloader {
    private static final String ip = Provider.getInstance().getCILENT_IP();
    private static final String rootAddress = "/geno/TS/api/rest";
    private static final String reloadTariffURL = "/admin/system/tariff/reload";
    private static final String reloadRadiusURL = "/admin/system/searchRadius/reload";
    private static final String reloadServiceURL = "/admin/system/services/active/reload";
    private static final String reloadSystemSettingURL = "/admin/system/setting/reload";
    private static final String reloadTicketSubjectURL = "/admin/system/tickets/subject/reload";

    public Status reload(String auth,Character method) {
        String urlStr = null;
        switch (method){
            case 'T':
                urlStr = ip + rootAddress + reloadTariffURL;
                break;
            case 'R':
                urlStr = ip + rootAddress + reloadRadiusURL;
                break;
            case 'S':
                urlStr = ip + rootAddress + reloadServiceURL;
                break;
            case 'Y':
                urlStr = ip + rootAddress + reloadSystemSettingURL;
                break;
            case 'J':
                urlStr = ip + rootAddress + reloadTicketSubjectURL;
                break;
            default:
                return Status.BAD_DATA;
        }

        try {
            URL obj = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.addRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization",auth);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return Status.OK;
            } else {
                return Status.UNKNOWN_ERROR;
            }
        }catch (IOException e){
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        }
    }
}
