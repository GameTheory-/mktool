# Description
~~~~
mktool v1.2

mktool is for unpacking & repacking the android boot,
recovery, or lok images and also for loki patching.

Note:
	mktool has been tested on ubuntu linux.
	should work on most debian based linux distros.
~~~~

# Usage
~~~~
1. Place a single boot, recovery, or lok image in the
mktool root directory.

2. Open terminal to the mktool root directory and run
any of the following commands:

Help:
	$ ./mktool h

Image Info:
	$ ./mktool i

Unpack Image:
	$ ./mktool u

Repack Image:
	$ ./mktool r

Loki Patch:
	$ ./mktool k

License:
	$ ./mktool l

- After unpacking, you can make any changes needed (ie.
edit ramdisk contents or add new zImage etc).

- After repacking, your new image will be named "new-image.img".
You can rename the new image to your liking.

Note:
	You can only have a single ".img" or ".lok" file in the mktool root
	directory at any given time. Unless you are loki patching which
	requires a boot.img or recovery.img along with your device aboot.img.
~~~~

![image](tools/rdir.png)

# mktool Author
- [GameTheory](https://github.com/GameTheory-)


**Resources:**
- [mkbootimg](https://github.com/GameTheory-/mkbootimg)
- [loki](https://github.com/GameTheory-/loki)
