package assignment11;

import java.io.InputStream;
import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import studio4.SerialComm;

public class WeatherStation {

	final private SerialComm port;
	int potenResult = 0;
	static long periodLength = 60000;
	static long nextTime = 0;

	public WeatherStation(String portname) {
		port = new SerialComm(portname);
	}

	public static void main(String[] args) throws Exception {	
		WeatherStation weather = new WeatherStation("/dev/cu.usbserial-DN01JD5X");
		while (true){
			long currentTime = System.currentTimeMillis();
			if (currentTime > nextTime){
				nextTime = currentTime+periodLength;
				weather.sendGet();
			}
		}
	}

	public String findLoc(int val){
		String location = "";
		String lopata = "https://api.darksky.net/forecast/d85382b55e37887d209f4a626d262077/38.649196, -90.306099";
		String home = "https://api.darksky.net/forecast/d85382b55e37887d209f4a626d262077/-50.492537, 140.901813";
		String nmh = "https://api.darksky.net/forecast/d85382b55e37887d209f4a626d262077/42.666360, -72.485175";

		if(val == 0){
			location = lopata;
		}
		else if (val ==  1023){

			location = nmh;
		}
		else {

			location = home;
		}
		return location;
	}

	// HTTP GET request
	private void sendGet() throws Exception {

		while(true){
			while (port.available()) {
				if(port.readByte() == (byte) 0x21){
					byte var = port.readByte();
					switch(var){
					default:
						break;

					case 0x30:
						byte potenbyte1 = port.readByte();
						byte potenbyte2 = port.readByte();
						int potenshift =  (potenbyte1 & 0xff) << 8;
						potenResult = potenshift + (potenbyte2 & 0xff);
						break;
					}
				}
			}

			String where = findLoc(potenResult);

			URL locUrl = new URL(where);
			HttpURLConnection locCon = (HttpURLConnection)locUrl.openConnection();
			locCon.setRequestMethod("GET");
			locCon.setRequestProperty("User-Agent", "HTTP/1.1");
			int resCode = locCon.getResponseCode();
			InputStream in = locCon.getInputStream();
			DataInputStream dataIn = new DataInputStream(in);	
			String dataInread = dataIn.readLine();
			System.out.println(dataInread);
			in.close();
			String[] arr = dataInread.split(",");
			String icon="";
			for(int i = 0; i < arr.length; i++){
				if(icon == ""){
					if(arr[i].contains("icon")){
						icon = arr[i].substring(arr[i].lastIndexOf(":"));
						icon = icon.replace(":","");
						icon = icon.replace("\"","");
					}
				}
			}
			char weatherChar = '\0';

			switch(icon){
			case("clear-day"):
				weatherChar = 'S';
			break;
			case("clear-night"):
				weatherChar = 'S';
			break;
			case("rain"):
				weatherChar = 'P';
			break;
			case("snow"):
				weatherChar = 'P';
			break;
			case("sleet"):
				weatherChar = 'P';
			break;
			case("wind"):
				weatherChar = 'W';
			break;
			case("fog"):
				weatherChar = 'F';
			break;
			case("cloudy"):
				weatherChar = 'C';
			break;
			case("partly-cloudy-day"):
				weatherChar = 'C';
			break;
			case("partly-cloudy-night"):
				weatherChar = 'C';
			break;
			default:
				System.out.println("Unknown key used");
				break;
			}

			port.writeByte((byte)0x23); //#
			port.writeByte((byte)weatherChar);
		}
	}
}
