package com.raspoid;

import com.pi4j.io.i2c.I2CFactory;
import com.raspoid.Tools;
import com.raspoid.MPU6050;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

/**
 * Example of use of an MPU6050.
 *
 * @see MPU6050
 *
 * @author Julien Louette &amp; Ga&euml;l Wittorski
 * @version 1.0
 */
public class MPU6050Example {

	public static String ipAdress = "192.168.3.133";
	public static CoapClient coapClient;
	public static CoapResponse coapResponse;
	public static JSONObject jsonObject;
	public static String json;

	/**
	 * Private constructor to hide the implicit public one.
	 */
	private MPU6050Example() {
	}

	/**
	 * Command-line interface.
	 *
	 * @param args unused here.
	 */
	public static void main(String[] args) throws I2CFactory.UnsupportedBusNumberException {
		MPU6050 mpu6050 = new MPU6050();
		mpu6050.startUpdatingThread();

		while (true) {
			Tools.log("-----------------------------------------------------");

			// Accelerometer
			Tools.log("Accelerometer:");
			double[] accelAngles = mpu6050.getAccelAngles();
			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(accelAngles[0]),
							MPU6050.angleToString(accelAngles[1]), MPU6050.angleToString(accelAngles[2])));

			double[] accelAccelerations = mpu6050.getAccelAccelerations();
			Tools.log("\tAccelerations: " + MPU6050.xyzValuesToString(MPU6050.accelToString(accelAccelerations[0]),
							MPU6050.accelToString(accelAccelerations[1]), MPU6050.accelToString(accelAccelerations[2])));

			// Gyroscope
			Tools.log("Gyroscope:");
			double[] gyroAngles = mpu6050.getGyroAngles();
			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(gyroAngles[0]),
							MPU6050.angleToString(gyroAngles[1]), MPU6050.angleToString(gyroAngles[2])));

			double[] gyroAngularSpeeds = mpu6050.getGyroAngularSpeeds();
			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angularSpeedToString(gyroAngularSpeeds[0]),
							MPU6050.angularSpeedToString(gyroAngularSpeeds[1]), MPU6050.angularSpeedToString(gyroAngularSpeeds[2])));

			// Filtered angles
			Tools.log("Filtered angles:");
			double[] filteredAngles = mpu6050.getFilteredAngles();
			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(filteredAngles[0]),
							MPU6050.angleToString(filteredAngles[1]), MPU6050.angleToString(filteredAngles[2])));
			mouseMove((int) filteredAngles[0], (int) filteredAngles[2]);

			Tools.sleepMilliseconds(100);
		}

	}

	public static void mouseMove(int x, int y) {
		int axisX = x;
		int axisY = y;
        
		jsonObject = new JSONObject();
		jsonObject.put("command", "change");
		jsonObject.put("axisX", String.valueOf(axisX));
		jsonObject.put("axisY", String.valueOf(axisY));
		json = jsonObject.toString();

		coapClient = new CoapClient();
		coapClient.setURI("coap://" + ipAdress + "/mouse");
		coapResponse = coapClient.post(json, MediaTypeRegistry.APPLICATION_JSON);
		coapClient.shutdown();
	}

}
