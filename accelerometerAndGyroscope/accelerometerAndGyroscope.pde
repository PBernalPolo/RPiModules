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


LSM9DS1_AG accelGyro;


void setup() {
  
  // object creation:
  // if you do not know the device direction, simply use
//  accelGyro = new LSM9DS1_AG();
  // however, if you know the device direction it will be faster (and cleaner if you look at the console) to use
  accelGyro = new LSM9DS1_AG( 0x6A );
  
  // some possible operations:
  accelGyro.reset();  // to reset the device using software
  accelGyro.reboot();  // to reboot the memory of the device
  
  // settings:
  // measurement range settings
  accelGyro.set_accelMeasurementRange( 3 );  // ( mode=0 -> 2g;  mode=1 -> 4g;  mode=2 -> 8g;  mode=3 -> 16g )
  accelGyro.set_gyroMeasurementRange( 2 );  // ( mode=0 -> 245dps;  mode=1 -> 500dps;  mode=2 -> 2000dps )
  // data rate settings
  accelGyro.set_accelDataRate( 6 );  // ( mode=0 -> 0Hz;  mode=1 -> 10Hz;  mode=2 -> 50Hz;  mode=3 -> 119Hz;  mode=4 -> 238Hz;  mode=5 -> 476Hz;  mode=6 -> 952Hz )
  accelGyro.set_gyroDataRate( 6 );  // ( mode=0 -> 0Hz;  mode=1 -> 14.9Hz;  mode=2 -> 59.5Hz;  mode=3 -> 119Hz;  mode=4 -> 238Hz;  mode=5 -> 476Hz;  mode=6 -> 952Hz )
  // update operation
  accelGyro.set_nonContinuousDataUpdate();  // (this feature prevents the reading of LSB and MSB related to different samples)
//  accelGyro.set_continuousDataUpdate();  // if you do not mind if you are reading the LSB and the MSB changes
  
}


void draw() {
  
  // to obtain measurements
  short[] accelData = accelGyro.get_accelData();
  short[] gyroData = accelGyro.get_gyroData();
  short temp = accelGyro.get_temperatureData();
  
  println( accelData[0] + " " + accelData[1] + " " + accelData[2] + " " + gyroData[0] + " " + gyroData[1] + " " + gyroData[2] + " " + temp );
  delay(500);
}
