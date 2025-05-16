# mktool v5.4

![image](tools/menu.png)

mktool is for unpacking & repacking the android boot,
recovery, or loki images and also for loki patching.

- Made for use on Linux.
- Must have java 8 or higher.

### Usage

In a terminal run the following command:
`./run.sh`

Input images must be in `./input/`, unpacking/repacking in
`./extracted/`, and output images will go into `./output/`.

### Project page

<https://techstop.github.io/mktool/>

### Latest download

<https://github.com/GameTheory-/mktool/releases>

### Building mktool

If you would like to build mktool yourself follow these steps.
1. Open mktool in Intellij Idea.
2. Setup the project to your liking.
3. Click on Build > Build Artifacts > main:jar > Build
4. Your jar archive will be in the "out/artifacts" directory.

### Resources

- [Apache Commons IO Library](https://mvnrepository.com/artifact/commons-io/commons-io)
- [mkbootimg](https://github.com/osm0sis/mkbootimg)
- [loki](https://github.com/djrbliss/loki)
