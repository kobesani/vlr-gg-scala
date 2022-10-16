#! /usr/bin/env python3

import argparse
import io
import subprocess
import sys
import time


if __name__ == "__main__":

    desc = """
        Python wrapper around Scala-based JAR file passing in command line args
    """

    parser = argparse.ArgumentParser(prog="run-vlr-scraper.py", description=desc)

    parser.add_argument(
        "--jar-path",
        type=str,
        required=False,
        default="/opt/scripts/out.jar",
        help="path to jar file (default is from alpine docker image, see Dockerfile.alpine)",
    )

    parser.add_argument(
        "--log-path",
        type=str,
        required=False,
        default="/opt/out.log",
        help="path to logfile (default is from alpine docker image, see Dockerfile.alpine)",
    )

    parser.add_argument(
        "--page-number",
        type=str,
        required=False,
        default="1",
        help="Match results page to request",
    )

    args = parser.parse_args()

    command = ["java", "-jar", f"{args.jar_path}", args.page_number]

    with (
        io.open(args.log_path, "wb") as writer,
        io.open(args.log_path, "rb") as reader
    ):
        process = subprocess.Popen(command, stdout=writer)
        while process.poll() is None:
            sys.stdout.write(reader.read().decode("utf-8"))
            time.sleep(0.5)
        # Read the remaining
        sys.stdout.write(reader.read().decode("utf-8"))
