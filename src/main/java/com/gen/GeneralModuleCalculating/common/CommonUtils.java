package com.gen.GeneralModuleCalculating.common;

import com.gen.GeneralModuleCalculating.services.ErrorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//This comment
public class CommonUtils {
    static ErrorsService errorsService = new ErrorsService();

    public static void waiter(int timeoutInMS){
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMS);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

        public static String standardIdParsingBySlice(String strBeforeId, String processedString) {
        return (processedString.replaceAll(".*" + strBeforeId, "").replaceAll("/.*", ""));
    }

    public static String standardIdParsingByPlace(Integer idPosition, String processedString) {
        String[] splittedString = processedString.split("/");
        return splittedString[idPosition];
    }
}
