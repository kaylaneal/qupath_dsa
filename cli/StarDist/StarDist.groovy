//import qupath.tensorflow.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*
import qupath.ext.stardist.StarDist2D
setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');



// Specify the model directory (you will need to change this!)
def pathModel = '../models/he_heavy_augment'


//detections
def stardist = StarDist2D.builder(pathModel)
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      .cellExpansion(3.0)          // Approximate cells based upon nucleus expansion
      .cellConstrainScale(1.5)     // Constrain cell expansion using nucleus size
      .measureShape()              // Add shape measurements
      .measureIntensity()          // Add cell measurements (in all compartments)
      .includeProbability(true)    // Add probability as a measurement (enables later filtering)
      .nThreads(10)
      .build()


// Run detection for the selected objects
def imageData = getCurrentImageData()

def server = getCurrentImageData().getServer()
// get dimensions of slide

minX = 0
minY = 0
maxX = server.getWidth()
maxY = server.getHeight()
// create rectangle roi (over entire area of image) for detections to be run over
def plane = ImagePlane.getPlane(0, 0)
def roi = ROIs.createRectangleROI(minX, minY, maxX-minX, maxY-minY, plane)
def annotationROI = PathObjects.createAnnotationObject(roi)
addObject(annotationROI)
selectAnnotations();

def pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)



def filename = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())


// output in geojson (warning - very large)
// boolean prettyPrint=true
// def gson = GsonTools.getInstance(prettyPrint)
// def output_detections_filepath = "/detections/" + filename + "_stardist_detections.geojson"
// def celldetections = getDetectionObjects()
// // print(celldetections)
// new File(output_detections_filepath).withWriter('UTF-8'){
//     gson.toJson(celldetections, it)
// }

// output in tsv form

detection_measurements_filepath = "../detections/" + filename + "_stardist_detections_and_measurements.tsv"
saveDetectionMeasurements(detection_measurements_filepath)

println 'Done!'