#!/bin/sh

# Make sure this script is executable to run it.
# chmod +x run.sh

# Location of this script & mktool.jar
BASE=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
# Tools directory
TOOLS_DIR=$BASE/tools

# Check if tools are executable. If not, make executable.
if [ ! -x "$TOOLS_DIR"/mkbootimg ]; then
	chmod +x "$TOOLS_DIR"/mkbootimg
fi
if [ ! -x "$TOOLS_DIR"/unpackbootimg ]; then
	chmod +x "$TOOLS_DIR"/unpackbootimg
fi
if [ ! -x "$TOOLS_DIR"/loki ]; then
	chmod +x "$TOOLS_DIR"/loki
fi

if command -v java > /dev/null; then
  # Path to java in host system
  JDK=$(command -v java)
  # Run the java program
  "$JDK" -jar mktool.jar
else
  echo "Could not find java installation!"
  echo "Please make sure you have java 8 or higher installed on your system."
  echo "You can get OpenJDK java from https://aws.amazon.com/corretto/"
fi
