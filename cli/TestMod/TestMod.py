import os
import subprocess
import datetime
import numpy as np
import json

from histomicstk.cli import utils

'''

Testing Module

'''

def validate_args(args):
    if not os.path.isfile(args.inputImageFile):
        raise OSError("Input image file does not exist.")
    
    elif not args.inputModelFile == None and not os.path.isfile(args.inputModelFile) :
        raise OSError("Model file does not exist.")
    
    elif not os.path.isfile(args.inputGroovyFile):
        raise OSError("Groovy Script does not exist.")
    
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    else:
        print('\n *** Arguments Validated: *** \n')


def validate_json(json_output):
    out = json_output.read()
    try:
       j = json.loads(out)
       return True, j
    except ValueError as err:
        print("\n *** JSON Output Invalid *** \n")
        return False, out

def main(args):
    # Check Arguments:
    validate_args(args)

    print('\n *** CLI Parameters: *** \n', args)
    
    # Define ROI
    if np.all( np.array(args.analysis_roi) == -1 ):
        roi = "0"
        print(f'\n *** Use ROI? No. *** \n')
    else:
        roi = ','.join( map( str, args.analysis_roi ) )
        print(f'\n *** Use ROI? Yes. *** \n')


    print("\n *** Run QuPath Script: *** \n")

    subprocess.run([
            "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
            "script", args.inputGroovyFile,
            "--image", args.inputImageFile,
            "-a", str(args.inputModelFile),
            "-a", args.outputAnnotationFile,
            "-a", str(args.outputItemMetadata),
            "-a", roi
    ])


    with open(args.outputItemMetadata, 'r') as jsonfile:
       # print(f"JSON Metadata File Opened: {jsonfile.read()}")
        is_json, json_out = validate_json(jsonfile)
        
        if is_json:
           final_json = {
                f"{os.path.splitext(os.path.basename(args.inputGroovyFile))[0]}_{datetime.date.today()}" : json_out
            }
        
        else:
            final_json = {
                f"{os.path.splitext(os.path.basename(args.inputGroovyFile))[0]}_{datetime.date.today()}" : {
                    "invalid JSON" : json_out
                }
            }

    with open(args.outputItemMetadata, 'w+') as outfile:
        outfile.write(json.dumps(final_json))

if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())


# LOCAL TEST RUN: ./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath script TestMod/TestMod.groovy --image ../60.svs -a ../classifiers/tissueDetect.json -a ../60a.json -a 1744,8290,18917,17937