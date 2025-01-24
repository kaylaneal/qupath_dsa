import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.regions.ImagePlane
import qupath.lib.objects.PathObjects
import qupath.lib.roi.ROIs

import com.google.gson.Gson

// Script Arguments:
println "Script Arguments: $args"

def modelFile = args[0]
def annotationPath = args[1]
def metadataPath = args[2]

def roiCoords = args[3]

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Define ROI if required:
if (roiCoords != "0"){
    double left = roiCoords.tokenize(',')[0] as double
    double top = roiCoords.tokenize(',')[1] as double
    double w = roiCoords.tokenize(',')[2] as double
    double h = roiCoords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def annot = PathObjects.createAnnotationObject(roi)

    addObject(annot)
    selectObjects(annot)
}

// Thresholder Model:
def json = new File(modelFile).text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.opencv.ml.pixel.OpenCVPixelClassifier)

createAnnotationsFromPixelClassifier(thresholder, 0, 0)

// Export Metadata:
Gson gson = new Gson()
File txtfile = new File(metadataPath)

txtfile.write( gson.toJson(server.getMetadata()).toString() )

// Export Annotations:
exportObjectsToGeoJson(getAnnotationObjects(), annotationPath, "FEATURE_COLLECTION")