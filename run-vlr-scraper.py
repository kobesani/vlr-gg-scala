#! /usr/bin/env python3

import argparse
import subprocess


if __name__ == "__main__":

    desc = """
        Python wrapper around Scala-based JAR file passing in command line args
    """

    parser = argparse.ArgumentParser(prog="run-vlr-scraper.py", description=desc)

    parser.add_argument(
        "--jar-path",
        type=str,
        required=False,
        default="/opt/out.jar",
        help="path to jar file (default is from alpine docker image, see Dockerfile.alpine)",
    )

    parser.add_argument(
        "--number-times",
        type=str,
        required=False,
        default="1",
        help="Number of times to print hello world :D",
    )

    args = parser.parse_args()

    out, err = subprocess.Popen(["java", "-jar", f"{args.jar_path}", args.number_times])

    print(out)
