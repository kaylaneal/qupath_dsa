// IMPORTS:
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.regions.RegionRequest
import qupath.lib.gui.tools.MeasurementExporter

// Script Arguments:                                [modelFile, AnnotationOutputPath, OtherOutputPath, ROI]
println "$args"
def modelFile = args[0]
def annotationOutPath = args[1]
def otherOutPath = args[2]

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Testing Exporting Images:
if (otherOutPath.contains("Image")) {
    println "Image Output Path: $otherOutPath"
    def dsReq = RegionRequest.createInstance(server, 10)
    writeImageRegion(server, dsReq, otherOutPath)
}
// Testing Text/Metadata File:
if (otherOutPath.contains("Text")) {
    println "Text Output Path: $otherOutPath"
    def txtfile = new File(otherOutPath)
    txtfile << server.getMetadata()
    def annotations = getAnnotationObjects()
    for (annot in annotations){
        txtfile << annot.getMeasurementList()
    }
}


exportObjectsToGeoJson(getAnnotationObjects(), annotationOutPath, "FEATURE_COLLECTION")