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

// TODO:
// - OFFSET_X_REG_L_M &&  OFFSET_X_REG_H_M && OFFSET_Y_REG_L_M && OFFSET_Y_REG_H_M && OFFSET_Z_REG_L_M && OFFSET_Z_REG_H_M
// - CTRL_REG4_M
// - STATUS_REG_M
// - INT_CFG_M && INT_SRC_M && INT_THS_L && INT_THS_H


//package RPiModules;


// class that implements the functionality of the magnetometer in the LSM9DS1
public class LSM9DS1_M extends I2C_Device {
  
  // PRIVATE VARIABLES
  
  // magnetometer registers addresses from datasheet
  private static final byte OFFSET_X_REG_L_M = (byte)0x05;
  private static final byte OFFSET_X_REG_H_M = (byte)0x06;
  private static final byte OFFSET_Y_REG_L_M = (byte)0x07;
  private static final byte OFFSET_Y_REG_H_M = (byte)0x08;
  private static final byte OFFSET_Z_REG_L_M = (byte)0x09;
  private static final byte OFFSET_Z_REG_H_M = (byte)0x0A;
  private static final byte WHO_AM_I_M = (byte)0x0F;
  private static final byte who_am_i_m_value = (byte)0b00111101;
  private static final byte CTRL_REG1_M = (byte)0x20;
  private static final byte CTRL_REG2_M = (byte)0x21;
  private static final byte CTRL_REG3_M = (byte)0x22;
  private static final byte CTRL_REG4_M = (byte)0x23;
  private static final byte CTRL_REG5_M = (byte)0x24;
  private static final byte STATUS_REG_M = (byte)0x27;
  private static final byte OUT_X_L_M = (byte)0x28;
  private static final byte OUT_X_H_M = (byte)0x29;
  private static final byte OUT_Y_L_M = (byte)0x2A;
  private static final byte OUT_Y_H_M = (byte)0x2B;
  private static final byte OUT_Z_L_M = (byte)0x2C;
  private static final byte OUT_Z_H_M = (byte)0x2D;
  private static final byte INT_CFG_M = (byte)0x30;
  private static final byte INT_SRC_M = (byte)0x31;
  private static final byte INT_THS_L = (byte)0x32;
  private static final byte INT_THS_H = (byte)0x33;
  
  
  // PUBLIC METHODS
  
  // creates a LSM9DS1 object, and sets its addresses automatically
  public LSM9DS1_M(){
    super();
    this.find_deviceAddress( this.who_am_i_m_value );
    if( this.get_deviceAddress() == -1 ){
      System.out.println( "LSM9DS1: not able to find magnetometer." );
    }else{
      System.out.println( "LSM9DS1: magnetometer found at " + Integer.toHexString( this.get_deviceAddress() ) );
    }
  }
  
  // creates a LSM9DS1 object, and sets its addresses
  public LSM9DS1_M( int i2cAddress ){
    super( i2cAddress );
    //this.magnetometer = new I2C_Device( i2cAddress );
    try{
      if( this.read_byteAt( this.WHO_AM_I_M ) != this.who_am_i_m_value ) System.out.println( "LSM9DS1: not sure if " + Integer.toHexString( i2cAddress ) + " is the magnetometer address. It seems to be other device." );
    }catch( Exception e ){
      System.out.println( "LSM9DS1: not able to find magnetometer in " + Integer.toHexString( i2cAddress ) );
    }
  }
  
  // sets the output data rate of the accelerometer ( mode=0 -> 0.625Hz;  mode=1 -> 1.25Hz;  mode=2 -> 2.5Hz;  mode=3 -> 5Hz;  mode=4 -> 10Hz;  mode=5 -> 20Hz;  mode=6 -> 40Hz;  mode=7 -> 80Hz )
  public void set_magnetometerDataRate( int mode ){
    // first, we select the continuous-conversion mode
    this.powerOn();
    // we read the register
    byte read = this.read_byteAt( this.CTRL_REG1_M );
    // we define the read mask
    byte readMask = (byte)0b11100011;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00000100 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b00001000 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b00001100 );
        break;
      case 4:
        toWrite = (byte)( (read & readMask) | 0b00010000 );
        break;
      case 5:
        toWrite = (byte)( (read & readMask) | 0b00010100 );
        break;
      case 6:
        toWrite = (byte)( (read & readMask) | 0b00011000 );
        break;
      case 7:
        toWrite = (byte)( (read & readMask) | 0b00011100 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG1_M , toWrite );
    
    return;
  }
  
  // sets the measurement range of the accelerometer ( mode=0 -> 4gauss;  mode=1 -> 8gauss;  mode=2 -> 12gauss;  mode=3 -> 16gauss )
  public void set_magnetometerMeasurementRange( int mode ){
    // we start reading the register
    byte read = this.read_byteAt( this.CTRL_REG2_M );
    // we define the read mask
    byte readMask = (byte)0b10011111;
    // we define what to write depending on the mode
    byte toWrite;
    switch( mode ){
      case 0:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
      case 1:
        toWrite = (byte)( (read & readMask) | 0b00100000 );
        break;
      case 2:
        toWrite = (byte)( (read & readMask) | 0b01000000 );
        break;
      case 3:
        toWrite = (byte)( (read & readMask) | 0b01100000 );
        break;
      default:
        toWrite = (byte)( (read & readMask) | 0b00000000 );
        break;
    }
    // and we write it
    this.write_byteAt( this.CTRL_REG2_M , toWrite );
    
    return;
  }
  
  // reboots the memory content
  public void reboot(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2_M );
    // we change the bit to reboot the device
    reg |= 0b00001000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2_M , reg );
    try{
      Thread.sleep(1000);  // we wait a little for the device to reboot itself
    }catch( Exception e ){ }
    
    return;
  }
  
  // resets the device using software
  public void reset(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG2_M );
    // we change the bit to reboot the device
    reg |= 0b00000100;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG2_M , reg );
    try{
      Thread.sleep(1000);  // we wait a little for the device to reset itself
    }catch( Exception e ){ }
    
    return;
  }
  
  // activates the device (the device will be in continuous-conversion mode)
  public void powerOn(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG3_M );
    // we change the bit to activate the device
    reg &= 0b11111100;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG3_M , reg );
    
    return;
  }
  
  // activates the device (the device will be in single-conversion mode)
  public void powerOnSingleConversion(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG3_M );
    // we change the bit to activate the device
    reg &= 0b11111100;
    reg |= 0b00000001;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG3_M , reg );
    
    return;
  }
  
  // deactivates the device
  public void powerOff(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG3_M );
    // we change the bit to activate the device
    reg |= 0b00000011;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG3_M , reg );
    
    return;
  }
  
  // sets a continuous data update (which means that you could be reading the MSB and, before reading the LSB, the value could change)
  public void set_continuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG5_M );
    // we change the bit to set a continuous data update
    reg &= 0b10111111;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG5_M , reg );
    
    return;
  }
  
  // sets a non-continuous data update (this feature prevents the reading of LSB and MSB related to different samples)
  public void set_nonContinuousDataUpdate(){
    // we read the value of the register
    byte reg = this.read_byteAt( this.CTRL_REG5_M );
    // we change the bit to set a non-continuous data update
    reg |= 0b01000000;
    // finally, we write the new byte
    this.write_byteAt( this.CTRL_REG5_M , reg );
    
    return;
  }
  
  // gets magnetometer data
  public short[] get_magnetometerData(){
    // we read the bytes
    byte[] buffer = this.read_bytesAt( this.OUT_X_L_M , 6 );
    // we compose each pair of bytes to return them as shorts (we do -x and -y so that the reference system is right-handed)
    return new short[]{  (short)-( ( buffer[1] << 8 ) | (buffer[0] & 0xFF) )  ,  (short)-( ( buffer[3] << 8 ) | (buffer[2] & 0xFF) )  ,  (short)( ( buffer[5] << 8 ) | (buffer[4] & 0xFF) )  };
  }
  
}
