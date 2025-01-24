# QuPath DSA Support Module

## INSTALL
To build docker image: `docker build -t qp-dsa .`
- to test locally run `docker run -it --entrypoint /bin/bash qp-dsa` to enter docker container. 

QuPath for Linux is expected to be downloaded in the project directory *BEFORE* building Docker image.
- QuPath Downloading Instructions: [QuPath GitHub](https://github.com/qupath/qupath/releases/tag/v0.5.1)
- Locally saved to `qpbin` folder within base directory

Upload "kaylaneal/qupath-dsa:latest" CLI to DSA.

## USAGE
*Command line modules are in [cli](/cli/) folder*

**QuPath Module**
- Run QuPath scripts through the DSA
- *Development Documentation can be found [here](docs/QP-DSA-dev.md)*
- QuPath Groovy scripts are uploaded to DSA and can be run on the image opened in HistomicsUI
    - All Groovy scripts should be able to accept and process the following input parameters, defined by values in the XML connected to the QuPath module *(in this order)*:
        - **input_model**: A JSON file describing, in QuPath format, the model being used in the analysis. 
        - **output_annotation**: The path to the annotation file that is to be created from the analysis.
        - **output_metadata**: The path to the metadata file that is to be created from the analysis.
        - **analysis_roi**: The region of interest to be analyzed.
    - Necessary files should be saved on the DSA directly
        - currently: upload files to user directory to make accessible from HistomicsUI interface.
- The output files are saved as metadata/annotation fields connected to the image item entry in the DSA.


### AVALIABLE SCRIPTS FOR QUPATH MODULE

**TissueDetect**
- Create tissue mask from pixel classifier using thresholding.
    - To run thresholder on a specific region, create an ROI by selecting the rectangle by Analysis ROI inout and draw a box over the region.
        - The entire image is processed as a default.
- Threshold Model File Required
    - Created locally with standard QuPath UI. 
        - Classify --> Pixel Classification --> Create Thresholder
        - Thresholder file is saved in project directory
            - ProjectFolder --> classifiers --> pixel_classifiers --> thresholder.json

**CellDetection**
- Create annotations for each cell found in a ROI.
    - Cells are detected via nuclei.
    - A ROI *must* be defined. The default is set to the entire image and may cause performance issues.
- Cell Detection Model File Required
    - Created from scratch. Keys found by running in QuPath UI and then using "Create Script".

### PRE-BUILT EXAMPLE MODULES

**TissueDetect**
- Create tissue mask from pixel classifier using thresholding.
    - To run thresholder on a specific region, create an ROI by selecting the rectangle by Analysis ROI input and draw a box over the region.
        - The entire image is processed as a default.
- Thresholding Pixel Classifier Model values can be input in the HistomicsUI TissueDetect Module under the header "Thresholder Model":
    - Minimum Area: Minimum size of region in &mu;m^2 to keep.
    - Minimum Hole Area: Minimum size of hole in &mu;m^2 to keep.
    - Downsample Factor: 16, 32, or 64 factor image resolution is downsampled.  
    - Channel: Image channel to threshold. 
    - Threshold: The pixel value that defines the cutoff to be considered tissue or background. 
- Image Metadata is returned.

**CellDetection**
- Create annotations for each cell found in a ROI.
    - Cells are detected via nuclei.
    - A ROI *must* be defined. The default is set to the entire image and may cause performance issues.
- Cell Detection Model values can be input in the HistomicsUI CellDetection Module under the header "Cell Detection Model":
    - Requested Pixel Size: Pixel size in &mu;m to perform detection. 
        - Higher values can be faster but less accurate.
    - Background Radius: Radius in &mu;m for background estimation. 
        - This value should be greater than the largest nuclei radius. 
        - Values <= 0 will turn off background subtraction.
    - Background Opening by Reconstruction: True/False value to use Opening-by-Reconstruction for backgound estimation.
    - Median Radius: Radius in &mu;m of median filter to use to reduce image texture.
    - Sigma: Value in &mu;m of Gaussian filter to use to reduce image noise.
        - Higher values stop nuclei from being fragmented but may reduce boundary accuracies.
    - Minimum Area: Minimum size of nuclei area in &mu;m^2 to keep.
    - Maximum Area: Maximum size of nuclei area in &mu;m^2 to keep.
    - Threshold: Mean intensity of nuclei to keep.
    - Maximum Background Intensity: Maximum backround intensity for detected nuclei with a backround radius greater than 0.
    - Cell Expansion: Amount in &mu;m by which to expand detected nuclei to approximate the full cell area.
    - Smooth Boundaries: True/False value to smooth detected cell boundaries.
    - Make Measurements: True/False value to add shape and intensity measurements to detections. 
- Annotations of detected cells is returned. 