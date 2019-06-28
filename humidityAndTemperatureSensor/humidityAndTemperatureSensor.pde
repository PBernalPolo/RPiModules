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


HTS221 humTemp;


void setup() {
  
  // object creation:
  // if you do not know the device direction, simply use
//  humTemp = new HTS221();
  // however, if you know the device direction it will be faster (and cleaner if you look at the console) to use
  humTemp = new HTS221( 0x5F );
  
  // some possible operations:
  humTemp.reboot();  // to reboot the memory of the device
  // power-off the device
  humTemp.powerOff();
  // power-on the device
  humTemp.powerOn();
  
  // settings:
  // humidity resolution
  humTemp.set_humidityResolution( 4 );  // number of samples taken to average. The interval is [4,512]
  // temperature resolution
  humTemp.set_temperatureResolution( 2 );  // number of samples taken to average. The interval is [2,256]
  // data rate settings
  humTemp.set_outputDataRate( 0 );  // ( mode=0 -> one-shot;  mode=1 -> 1Hz;  mode=2 -> 7Hz;  mode=3 -> 12.5Hz )
  // update operation
  humTemp.set_nonContinuousDataUpdate();  // (this feature prevents the reading of LSB and MSB related to different samples)
//  humTemp.set_continuousDataUpdate();  // if you do not mind if you are reading the LSB and the MSB changes
  
  // one-shot measurement taking:
  for(int i=0; i<5; i++){
    // first, the measurements are updated
    humTemp.update_measurements();
    // then, they are read
    short hum = humTemp.get_humidityData();
    short temp = humTemp.get_temperatureData();
    
    println( hum + " " + temp );
    delay(100);
  }
  
  // now, let's try the automatic measurement updating
  humTemp.set_outputDataRate( 3 );
}


void draw() {
  // to obtain measurements
  short hum = humTemp.get_humidityData();
  short temp = humTemp.get_temperatureData();
  
  println( hum + " " + temp );
  delay(500);
}
