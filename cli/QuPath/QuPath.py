import os
import subprocess
import datetime
import numpy as np
import json

from histomicstk.cli import utils

'''

Use custom QuPath Groovy scripts to run image analyses.

'''

def validate_args(args):
    if not os.path.isfile(args.input_image):
        raise OSError("Input image file does not exist.")
    
    elif args.inputModelFile == None and not os.path.isfile(args.inputModelFile):
        raise OSError("Model file does not exist.")
    
    elif not os.path.isfile(args.input_script):
        raise OSError("Groovy Script does not exist.")
    
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI must be a vector of 4 elements.")
    
    else:
        print("\n *** Arguments Validated: *** \n")

def validate_json(json_output):
    out = json_output.read()
    try:
        j = json.loads(out)
        return True, j
    except:
        print("\n *** JSON Metadata Output File Invalid *** \n")
        return False, out

def main(args):
    # Check Arguments
    print("\n ** CLI Arguments: ** \n", args)
    validate_args(args)

    # Define ROI:
    if np.all( np.array( args.analysis_roi ) == -1 ):
        roi = "0"
        print("\n *** Use ROI or Whole Image? Whole Image. *** \n")
    else:
        roi = ','.join( map( str, args.analysis_roi ) )
        print("\n *** Use ROI or Whole Image? ROI. *** \n")

    # Run Script:
    print('\n ** Run QuPath Script: ** \n')
    subprocess.run([
        "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
        "script", args.input_script,
        "--image", args.input_image,
        "-a", str(args.inputModelFile),
        "-a", args.outputAnnotationFile,
        "-a", args.outputItemMetadata,
        "-a", roi
    ])

    # Write JSON Metadata File:
    if os.path.isfile(args.outputItemMetadata):
        with open(args.outputItemMetadata, 'r') as jsonfile:
            is_json, json_out = validate_json(jsonfile)

            now = str( datetime.datetime.now() ).split('.')[0]

            if is_json:
                final_json = { f"{os.path.splitext(os.path.basename(args.input_script))[0]}_{now}" : json_out }
            else:
                final_json = { f"{os.path.splitext(os.path.basename(args.input_script))[0]}_{now}" : { "invalid JSON" : json_out } }
    else:
        final_json = { f"{os.path.splitext(os.path.basename(args.input_script))[0]}_{str( datetime.datetime.now() ).split('.')[0]}": f"{os.path.basename(args.input_script)} Successful."}
    
    with open(args.outputItemMetadata, 'w+') as outfile:
        outfile.write( json.dumps(final_json) )


if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())