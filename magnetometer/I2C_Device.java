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


//package RPiModules;


// COPY THE FOLLOWING 2 LINES TO THE MAIN FILE
// sudo raspi-config -> Interfacing Options -> I2C -> <Yes> -> sudo reboot
import processing.io.*;  // Sketch -> Import Library... -> Add Library... -> Libraries -> Hardware I/O


// class that implements the basic operations for an I2C device
public class I2C_Device {
  
  // PRIVATE VARIABLES
  private byte deviceAddress;
  private I2C i2c;
  
  
  // PUBLIC METHODS
  
  // creates an I2C_Device
  public I2C_Device(){
    this.i2c = new I2C(I2C.list()[0]);
  }
  
  // creates an I2C_Device, and sets its address
  public I2C_Device( int theDeviceAddress ){
    this.i2c = new I2C(I2C.list()[0]);
    this.deviceAddress = (byte)theDeviceAddress;
  }
  
  // finds the address of the device with a given WHO_AM_I value
  public void find_deviceAddress( byte theWAI ){
    for(byte iAddress=0; iAddress<127; iAddress++){
      try{
        this.i2c.beginTransmission( iAddress );
        this.i2c.write( 0x0F );  // this is the register address that usually contains the WHO_AM_I value
        byte WAI = this.i2c.read(1)[0];
        if( theWAI == WAI ){
          this.deviceAddress = iAddress;
          return;
        }
      }catch( Exception e ){
        continue;
      }
    }
    this.deviceAddress = -1;
  }
  
  // sets the address of the device
  public void set_deviceAddress( byte theDeviceAddress ){
    this.deviceAddress = theDeviceAddress;
  }
  
  // gets the address associated with this device
  public byte get_deviceAddress(){
    return this.deviceAddress;
  }
  
  // reads a byte from a given register address
  public byte read_byteAt( byte registerAddress ){
    // we begin a transmission with the device with address this.deviceAddress
    this.i2c.beginTransmission( this.deviceAddress );
    // we select the register we want to read (see sensor documentation, I2C operation)
    this.i2c.write( registerAddress );
    // we read the register, and we return the read value (It is not necessary to call endTransmission() after read(). See Processing I2C)
    return this.i2c.read( 1 )[0];
  }
  
  // reads several bytes from a given register address
  public byte[] read_bytesAt( byte registerAddress , int nBytes ){
    // we begin a transmission with the device with address this.deviceAddress
    this.i2c.beginTransmission( this.deviceAddress );
    // we select the register we want to read (see sensor documentation, I2C operation)
    this.i2c.write( registerAddress );
    // we read the register, and we return the read value (It is not necessary to call endTransmission() after read(). See Processing I2C)
    return this.i2c.read( nBytes );
  }
  
  // writes a byte in a given register address
  public void write_byteAt( byte registerAddress , byte theByte ){
    // we begin a transmission with the device with address this.deviceAddress
    this.i2c.beginTransmission( this.deviceAddress );
    // we select the register we want to read (see sensor documentation, I2C operation)
    this.i2c.write( registerAddress );
    // we write the value in the register
    this.i2c.write( theByte );
    // finally, we end the transmission to execute the queued writes
    this.i2c.endTransmission();
  }
  
  // writes several bytes in a given register address
  public void write_bytesAt( byte registerAddress , byte[] theBytes ){
    // we begin a transmission with the device with address this.deviceAddress
    this.i2c.beginTransmission( this.deviceAddress );
    // we select the register we want to read (see sensor documentation, I2C operation)
    this.i2c.write( registerAddress );
    // we write the values in the register
    this.i2c.write( theBytes );
    // finally, we end the transmission to execute the queued writes
    this.i2c.endTransmission();
  }
  
  // prints the binary value of a register
  public void printReg( byte registerAddress ) {
    // we read the value of the register
    byte reg = this.read_byteAt( registerAddress );
    // we print
    System.out.println( String.format( "%8s" , Integer.toBinaryString( reg & 0xFF ) ).replace( ' ' , '0' ) );
  }
  
}
