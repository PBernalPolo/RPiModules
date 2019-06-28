/*
 * Copyright (C) 2017 P.Bernal-Polo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author P.Bernal-Polo
 */


// sudo apt-get install sense-hat
// sudo raspi-config -> Interfacing Options -> I2C -> <Yes> -> sudo reboot
import processing.io.*;  // Sketch -> Import Library... -> Add Library... -> Libraries -> Hardware I/O

//import RPiModules.*;


LSM9DS1_M magnetometer;


void setup() {
  
  // object creation:
  // if you do not know the device direction, simply use
//  magnetometer = new LSM9DS1_M();
  // however, if you know the device direction it will be faster (and cleaner if you look at the console) to use
  magnetometer = new LSM9DS1_M( 0x1C );
  
  // some possible operations:
  magnetometer.reset();  // to reset the device using software
  magnetometer.reboot();  // to reboot the memory of the device
  // power-off the device
  magnetometer.powerOff();
  // power-on the device
  magnetometer.powerOn();
  
  // settings:
  // measurement range settings
  magnetometer.set_magnetometerMeasurementRange( 3 );  // ( mode=0 -> 4gauss;  mode=1 -> 8gauss;  mode=2 -> 12gauss;  mode=3 -> 16gauss )
  // data rate settings
  magnetometer.set_magnetometerDataRate( 7 );  // ( mode=0 -> 0.625Hz;  mode=1 -> 1.25Hz;  mode=2 -> 2.5Hz;  mode=3 -> 5Hz;  mode=4 -> 10Hz;  mode=5 -> 20Hz;  mode=6 -> 40Hz;  mode=7 -> 80Hz )
  // update operation
  magnetometer.set_nonContinuousDataUpdate();  // (this feature prevents the reading of LSB and MSB related to different samples)
//  magnetometer.set_continuousDataUpdate();  // if you do not mind if you are reading the LSB and the MSB changes
  
}


void draw() {
  
  // to obtain measurements
  short[] magData = magnetometer.get_magnetometerData();
  
  println( magData[0] + " " + magData[1] + " " + magData[2] );
  delay(500);
}
