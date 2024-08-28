import os
import subprocess

from histomicstk.cli import utils

'''

Use custom QuPath Groovy scripts to run image analyses.

'''

def validate_args(args):
    if not os.path.isfile(args.inputImageFile):
        raise OSError("Input image file does not exist.")
    
    elif not os.path.isfile(args.inputModelFile):
        raise OSError("Model file does not exist.")
    
    elif not os.path.isfile(args.inputGroovyScript):
        raise OSError("Groovy Script does not exist.")
    
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    else:
        print('\n *** Arguments Validated: *** \n')

def main(args):
    print('\n ** CLI Arguments: ** \n', args)
    validate_args(args)

    subprocess.check_call([
        "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
        "script", args.inputGroovyScript,
        "--image", args.inputImageFile,
        "-a", args.inputModelFile,
        "-a", args.outputAnnotationFile
    ])

if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())