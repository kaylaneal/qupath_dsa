import os
import subprocess
import numpy as np

from histomicstk.cli import utils

'''

Testing Module

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
    validate_args(args)
    print('\n *** CLI Parameters: *** \n', args)
    
    use_roi = False if np.all(args.analysis_roi == -1) else True
    # print(f'\n *** Use ROI? {"yes" if use_roi else "no"}. *** \n')

    print("\n *** Run QuPath Script: *** \n")

    if use_roi:
        roi = ','.join( map( str, args.analysis_roi ) )
        subprocess.check_output([
            "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
            "script", "TestMod2/TestMod2.groovy", 
            "--image", args.inputImageFile,
            "-a", args.inputThresholderFile,
            "-a", args.outputTextFile,
            "-a", roi
        ])
    
    else:
         subprocess.check_output([
            "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
            "script", "TestMod2/TestMod2.groovy", 
            "--image", args.inputImageFile,
            "-a", args.inputThresholderFile,
            "-a", args.outputTextFile
        ])



if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())


# LOCAL TEST RUN: ./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath script TestMod/TestMod.groovy --image ../60.svs -a ../classifiers/tissueDetect.json -a ../60a.json -a 1176,7538,2008,19213