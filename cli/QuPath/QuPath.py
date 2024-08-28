import os
import subprocess
import numpy as np

from histomicstk.cli import utils

'''

Use custom QuPath Groovy scripts to run image analyses.

'''

def validate_args(args):
    if not os.path.isfile(args.inputImageFile):
        raise OSError("Input image file does not exist.")
    
    elif not os.path.isfile(args.inputModelFile):
        raise OSError("Model file does not exist.")
    
    elif not os.path.isfile(args.inputGroovyFile):
        raise OSError("Groovy Script does not exist.")
    
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    else:
        print('\n *** Arguments Validated: *** \n')

def main(args):
    print('\n ** CLI Arguments: ** \n', args)
    validate_args(args)

    use_roi = False if np.all( np.array( args.analysis_roi ) == -1 ) else True
    print(f'\n ** Use ROI? {"Yes" if use_roi else "No"}. ** \n')

    print('\n ** Run QuPath Script: ** \n')

    if use_roi:
        roi = ','.join( map( str, args.analysis_roi ) )
        subprocess.check_output([
            "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
            "script", args.inputGroovyFile,
            "--image", args.inputImageFile,
            "-a", args.inputModelFile,
            "-a", args.outputAnnotationFile,
            "-a", roi
        ])

    else:
        subprocess.check_output([
            "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
            "script", args.inputGroovyFile,
            "--image", args.inputImageFile,
            "-a", args.inputModelFile,
            "-a", args.outputAnnotationFile
        ])

if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())