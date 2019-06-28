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


LPS25H presTemp;


void setup() {
  
  // object creation:
  // if you do not know the device direction, simply use
//  presTemp = new LPS25H();
  // however, if you know the device direction it will be faster (and cleaner if you look at the console) to use
  presTemp = new LPS25H( 0x5C );
  
  // some possible operations:
  presTemp.reset();  // to reset the device using software
  presTemp.reboot();  // to reboot the memory of the device
  // power-off the device
  presTemp.powerOff();
  // power-on the device
  presTemp.powerOn();
  
  // settings:
  // pressure resolution
  presTemp.set_pressureResolution( 8 );  // number of samples taken to average. The interval is [8,512]
  // temperature resolution
  presTemp.set_temperatureResolution( 8 );  // number of samples taken to average. The interval is [8,64]
  // data rate settings
  presTemp.set_outputDataRate( 0 );  // ( mode=0 -> one-shot;  mode=1 -> 1Hz;  mode=2 -> 7Hz;  mode=3 -> 12.5Hz;  mode=4 -> 25Hz )
  // update operation
  presTemp.set_nonContinuousDataUpdate();  // (this feature prevents the reading of LSB and MSB related to different samples)
//  presTemp.set_continuousDataUpdate();  // if you do not mind if you are reading the LSB and the MSB changes
  
  // one-shot measurement taking:
  for(int i=0; i<5; i++){
    // first, the measurements are updated
    presTemp.update_measurements();
    // then, they are read
    int pres = presTemp.get_pressureData();
    short temp = presTemp.get_temperatureData();
    
    println( pres + " " + temp );
    delay(100);
  }
  
  // now, let's try the automatic measurement updating
  presTemp.set_outputDataRate( 4 );
}


void draw() {
  // to obtain measurements
  int pres = presTemp.get_pressureData();
  short temp = presTemp.get_temperatureData();
  
  println( pres + " " + temp );
  delay(500);
}
