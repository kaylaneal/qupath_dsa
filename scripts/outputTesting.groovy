// IMPORTS:
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.regions.RegionRequest
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.io.GsonTools
import qupath.lib.classifiers.pixel.PixelClassifier
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane

// Script Arguments:                                [modelFile, AnnotationOutputPath, OtherOutputPath, ROI]
println "$args"
def modelFile = args[0]
def annotationOutPath = args[1]
def otherOutPath = args[2]

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Tissue Segmentation:

def json = new File(modelFile).text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.opencv.ml.pixel.OpenCVPixelClassifier)

// Analyze Whole Image or ROI:
if ((args.length).is(4)){
    coords = args[3]
    double left = coords.tokenize(',')[0] as double
    double top = coords.tokenize(',')[1] as double
    double w = coords.tokenize(',')[2] as double
    double h = coords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def annot = PathObjects.createAnnotationObject(roi)

    addObject(annot)
    selectObjects(annot)

}

// Create + Apply Thresholder:
println "Running Thresholder"
double minArea = 0
double minHoleArea = 0

createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)


// Testing Exporting Images:
/*
if (otherOutPath.contains("Image")) {
    println "Image Output Path: $otherOutPath"
    def dsReq = RegionRequest.createInstance(server, 10)
    writeImageRegion(server, dsReq, otherOutPath)
}
*/
// Testing Text/Metadata File:
//if (otherOutPath.contains("Text")) {
    println "Text Output Path: $otherOutPath"
    def txtfile = new File(otherOutPath)
    txtfile << server.getMetadata()
    def annotations = getAnnotationObjects()
    for (annot in annotations){
        println annot.getMeasurements()
        txtfile << annot.getMeasurements()
    }
//}

// Export Annotations:
exportObjectsToGeoJson(getAnnotationObjects(), annotationOutPath, "FEATURE_COLLECTION")



// ./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath script QuPath/outputTesting.groovy --image ../60.svs -a ../classifiers/tissueDetect.json -a ../60Anot.json -a ../60Text.txt