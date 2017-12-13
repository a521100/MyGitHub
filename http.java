import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * Created by youxinyang on 2017/7/10.
 */

public class FakeClient {

    private CloseableHttpClient httpClient;
    private List<Header> headers = new ArrayList<Header>();

    public FakeClient(){

        httpClient = HttpClientBuilder.create().build();

    }

    public static void main(String[] args) throws Exception{

        //CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionTimeToLive(2, TimeUnit.SECONDS).setProxy(new HttpHost("172.20.10.12",8888)).setUserAgent("Dalvik/1.6.0 (Linux; U; Android 4.3; ZTE Q801L Build/JLS36C)").build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionTimeToLive(2, TimeUnit.SECONDS).setUserAgent("Dalvik/1.6.0 (Linux; U; Android 4.3; ZTE Q801L Build/JLS36C)").build();
        GzipDataBuilder builder = new GzipDataBuilder();
        builder.loadCdmaCells();

    //    Phone phone = GzipDataBuilder.phones.get(20);
        List<SiteInfo> siteInfos = new ArrayList<>();
        siteInfos.addAll(Constant.httpSiteMaps.values());
        siteInfos.remove(Constant.httpSiteMaps.get("百度"));
//        LteTestInfo testInfo = new LteTestInfo();
//        testInfo.setMcc(460);
//        testInfo.setMcc(11);
//        testInfo.setEnbId(704694);
//        testInfo.setSectorId(49);
//        testInfo.setTac(44056);
//        testInfo.setPci(139);
//        testInfo.setLat(25.057777);
//        testInfo.setLng(102.701027);

        for(LteTestInfo testInfo : getJobs()){
            while (true){
                try{
                    Phone phone = GzipDataBuilder.phones.get(Toolkits.getIntegerInRange(0, GzipDataBuilder.phones.size() - 1));
                    System.out.println(phone);

                    String reqXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<BusiMsg>\r\n<Auth>0000=" + phone.getImei() + "</Auth>" + "\r\n" + "<LogUploadRequest>" + "\r\n" + "<PlanName>SPS_LOG</PlanName>" + "\r\n" + "<LogClass>" + "bob" + "</LogClass>" + "\r\n" + "<LogSn>1</LogSn>" + "\r\n" + "</LogUploadRequest>" + "\r\n" + "</BusiMsg>" + "\r\n";

                    HttpPost post = new HttpPost("http://ctc.yotta800.com:9901/hmu");
                    post.addHeader("Encoding","gzip");
                    post.addHeader("Accept-Encoding","gzip");
                    post.addHeader("X-SECU",Toolkits.xs.get(phone.getPluginVer()));
                    post.addHeader("Charset","UTF-8");
                    post.addHeader("Connection", "close");
                    post.addHeader("Host","ctc.yotta800.com:9901");

                    byte[] data = builder.data(phone, siteInfos, testInfo);



                    String spliter = "P-hm9gr0ZBr-1d2xGZ2-ylhZl3-qZEZKLqQM1";
                    SimpleDateFormat format = new SimpleDateFormat("yyMMddhhmmssSSS");

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    writer.write("--" + spliter + "\r\n");
                    writer.write("Content-Type: application/X-aims-BusiMsg; charset=\"UTF-8\"\r\n\r\n");
                    writer.write(reqXML);
                    writer.write("--" + spliter + "\r\n");
                    writer.write("Content-Type: application/X-aims-OctetFile\r\n");
                    writer.write("Content-disposition: attachment; filename=\"/storage/sdcard0/CpsLite/" + phone.getImsi() + "_" + phone.getImei() + "_" + format.format(new Date(System.currentTimeMillis())) + "_bob_104_01.gz\"\r\n\r\n");
                    writer.flush();
                    outputStream.write(data);
                    outputStream.flush();
                    writer.write("\r\n--" + spliter + "\r\n");
                    writer.flush();
                    byte[] ct = outputStream.toByteArray();

                    ByteArrayEntity entity = new ByteArrayEntity(ct, ContentType.create("multipart/mixed").withParameters(new BasicNameValuePair("boundary",spliter)));
                    post.setEntity(entity);
                    //System.out.println(EntityUtils.toString(entity));

                    CloseableHttpResponse closeableHttpResponse  = httpClient.execute(post);

                    if(EntityUtils.toString(closeableHttpResponse.getEntity()).contains("<StatusCode>200</StatusCode>")){
                        System.out.println("Success!");
                    }else {
                        System.out.println("Failed!");
                        for(String l : org.apache.commons.io.IOUtils.readLines(new GZIPInputStream(new ByteArrayInputStream(data)),"UTF-8")){
                            System.out.println(l);
                        }
                        System.out.println(EntityUtils.toString(closeableHttpResponse.getEntity()));
                        System.out.println(closeableHttpResponse.getEntity().toString());
                    }
                    Thread.sleep(500);
                    closeableHttpResponse.close();
                    break;
                }catch (Exception e){
                    System.out.println(e.toString());

                }

            }
        }