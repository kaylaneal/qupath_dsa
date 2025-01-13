import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathAnnotationObject

// Script Arguments:
println "$args"

def annotationPath = args[0]
def roiCoords = args[1]
def detectionModel = args[2]

// Define ROI:
double left = roiCoords.tokenize(',')[0] as double
double top = roiCoords.tokenize(',')[1] as double
double w = roiCoords.tokenize(',')[2] as double
double h = roiCoords.tokenize(',')[3] as double

println  "ROI Coords: [$left, $top, $w, $h]"

def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
def annot = PathObjects.createAnnotationObject(roi)

addObject(annot)
selectObjects(annot)

// Run Cell Detection:
println "Running Cell Detection"
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', detectionModel)

// Export Detections:
exportAllObjectsToGeoJson(annotationPath, "FEATURE_COLLECTION")

// Export Cell Measurements to File ; return json to metadata of path to file
// measurement file path derived from annotation file path
println "Exporting Measurements from Cell Detection"

def measurementPath = annotationPath - '-outputAnnotationFile.json'
measurementPath = measurementPath.concat("-measurementsFile.tsv")
def measurementFile = new File(measurementPath)
def exporter = new MeasurementExporter()
                    .imageList([getCurrentImageName()])
                    .separator("\t")
                    .exportType(PathAnnotationObject.class)
                    .exportMeasurements(measurementFile)