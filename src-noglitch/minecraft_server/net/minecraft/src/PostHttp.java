package net.minecraft.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostHttp {
	public static String func_52016_a(Map<String, Object> map0) {
		StringBuilder stringBuilder1 = new StringBuilder();
		Iterator<?> iterator2 = map0.entrySet().iterator();

		while(iterator2.hasNext()) {
			Entry<?, ?> map$Entry3 = (Entry<?, ?>)iterator2.next();
			if(stringBuilder1.length() > 0) {
				stringBuilder1.append('&');
			}

			try {
				stringBuilder1.append(URLEncoder.encode((String)map$Entry3.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException6) {
				unsupportedEncodingException6.printStackTrace();
			}

			if(map$Entry3.getValue() != null) {
				stringBuilder1.append('=');

				try {
					stringBuilder1.append(URLEncoder.encode(map$Entry3.getValue().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException unsupportedEncodingException5) {
					unsupportedEncodingException5.printStackTrace();
				}
			}
		}

		return stringBuilder1.toString();
	}

	public static String func_52018_a(URL uRL0, Map<String, Object> map1, boolean z2) {
		return func_52017_a(uRL0, func_52016_a(map1), z2);
	}

	public static String func_52017_a(URL uRL0, String string1, boolean z2) {
		try {
			HttpURLConnection httpURLConnection4 = (HttpURLConnection)uRL0.openConnection();
			httpURLConnection4.setRequestMethod("POST");
			httpURLConnection4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection4.setRequestProperty("Content-Length", "" + string1.getBytes().length);
			httpURLConnection4.setRequestProperty("Content-Language", "en-US");
			httpURLConnection4.setUseCaches(false);
			httpURLConnection4.setDoInput(true);
			httpURLConnection4.setDoOutput(true);
			DataOutputStream dataOutputStream5 = new DataOutputStream(httpURLConnection4.getOutputStream());
			dataOutputStream5.writeBytes(string1);
			dataOutputStream5.flush();
			dataOutputStream5.close();
			BufferedReader bufferedReader6 = new BufferedReader(new InputStreamReader(httpURLConnection4.getInputStream()));
			StringBuffer stringBuffer8 = new StringBuffer();

			String string7;
			while((string7 = bufferedReader6.readLine()) != null) {
				stringBuffer8.append(string7);
				stringBuffer8.append('\r');
			}

			bufferedReader6.close();
			return stringBuffer8.toString();
		} catch (Exception exception9) {
			if(!z2) {
				Logger.getLogger("Minecraft").log(Level.SEVERE, "Could not post to " + uRL0, exception9);
			}

			return "";
		}
	}
}
