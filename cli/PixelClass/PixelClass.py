import os
import subprocess

from histomicstk.cli import utils

def main(args):
    print('\n *** CLI Parameters: *** \n')
    print(args)

    if not os.path.isfile(args.inputImageFile):
        raise OSError("Input image file does not exist.")
    
    if len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    print("\n *** Run QuPath Script: *** \n")

    with open(args.outputTextFile, 'w') as outfile:
        outfile.write(
            subprocess.check_output(
                ["./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
                 "script", "PixelClass/PixelClass.groovy", 
                    "--image", args.inputImageFile], 
                    shell = True, text = True
            )
        )


if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())