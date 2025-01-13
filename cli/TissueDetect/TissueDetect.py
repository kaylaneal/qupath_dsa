import os
import subprocess
import numpy as np
import json
import datetime

from histomicstk.cli.utils import CLIArgumentParser

'''

Create and Export Tissue Annotations as Defined by Input Parameters.

'''

def validate_args(args):
    if not os.path.isfile(args.input_image):
        raise OSError("Input image file does not exist.")
    
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
        print("\n *** INVALID JSON FORMAT: Metadata File *** \n")
        return False, out

def main(args):
    # Check Arguments:
    print("\n *** CLI Parameters: *** \n", args)
    validate_args(args)

    # Define ROI:
    if np.all( np.array(args.analysis_roi ) == -1 ):
        roi = "0"
        print("\n *** Use ROI or Whole Image? Whole Image. *** \n")
    else:
        roi = ','.join( map( str, args.analysis_roi ) )
        print("\n *** Use ROI or Whole Image? ROI. *** \n")

    # Image Channel:
    if (args.channel) == "Red":
        channel = 0
    elif (args.channel) == "Green":
        channel = 1
    elif (args.channel) == "Blue":
        channel = 2
    else:
        channel = 3

    # Run TissueDetect Script:
    print("\n *** Run QuPath Script: *** \n")

    subprocess.run([
        "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath", 
        "script", "TissueDetect/TissueDetect.groovy", 
        "--image", args.input_image, 
        "-a", args.outputAnnotationFile,
        "-a", args.outputItemMetadata,
        "-a", roi,
        "-a", str(args.min_area),
        "-a", str(args.min_hole_area),
        "-a", str(args.downsample),
        "-a", str(channel),
        "-a", str(args.threshold)
    ])

    # Write JSON Metadata File:
    with open(args.outputItemMetadata, 'r') as jsonfile:
        is_json, json_out = validate_json(jsonfile)
        now = str( datetime.datetime.now() ).split(".")[0]

        if is_json:
            final_json = { f"TissueDetect_{now}" : json_out }
        else:
            final_json = { f"TissueDetect_{now}" : { "invalid JSON" : json_out } }
    
    with open(args.outputItemMetadata, 'w+') as outfile:
        outfile.write( json.dumps(final_json) )


if __name__ == "__main__":
    main(CLIArgumentParser().parse_args())