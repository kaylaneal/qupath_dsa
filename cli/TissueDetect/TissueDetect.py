import os
import subprocess

from histomicstk.cli.utils import CLIArgumentParser

'''

Export Annotations Created by Pixel Threshold Classifier
- Annotation = Tissue Area Calculated via Selected Thresholder

'''

def validate_args(args):
    if not os.path.isfile(args.inputImageFile):
        raise OSError("Input image file does not exist.")

    elif not os.path.isfile(args.inputThresholderFile):
        raise OSError("Threshold model file does not exist.")
    
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    else:
        print('\n *** Arguments Validated: *** \n')

def main(args):
    print('\n *** CLI Parameters: *** \n')
    print(args)
    validate_args(args)

    print("\n *** Run QuPath Script: *** \n")

    subprocess.run([
        "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
        "script", "TissueDetect/TissueDetect.groovy", 
        "--image", args.inputImageFile, 
        "-a", args.inputThresholderFile,
        "-a", args.outputAnnotationFile,
    ])


if __name__ == "__main__":
    main(CLIArgumentParser().parse_args())