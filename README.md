# RPiModules
Modest classes that implement some functionality to read from Sense HAT I2C devices using Processing in Raspberry Pi

These classes use the Processing library "Hardware I/O" that can be downloaded doing:
- Sketch -> Import Library... -> Add Library... -> Libraries -> Hardware I/O

Previously, the code did not work for the pressure sensor (LPS25H) and the humidity sensor (HTS221).
Now, everything works.
I did not know why, but after looking at someone else code, I noticed that it was necessary to add 0x80 to the address of the values registers.
If anyone knows why it was necessary, please let me know.

DISCLAIMER: the SenseHAT could be permanently damaged if one performs certain operations.
This code is shared under the terms of the GNU General Public License.
What I mean is that is shared "WITHOUT ANY WARRANTY".
