#1/bin/bash

Help()
{
   # Display Help
   echo "Shell script has only one param - path to Neo4j resource input folder"
}

################################################################################
################################################################################
# Main program                                                                 #
################################################################################
################################################################################
################################################################################
# Process the input options. Add options as needed.                            #
################################################################################
# Get the options
while getopts ":h" option; do
   case $option in
      h) # display Help
         Help
         exit;;
     \?) # incorrect option
         echo "Error: Invalid option"
         exit;;
   esac
done

ARG1=${1:-"/mnt/e/spark-graphx/Neo4j"}

docker run --name neo4j --publish=7474:7474 --publish=7687:7687 --volume="$ARG1/neo4j/logs:/logs" --volume="$ARG1/neo4j/data:/data" --volume="$ARG1/neo4j/plugins:/plugins" --volume="$ARG1/neo4j/import:/import" --volume="$ARG1/conf:/conf" --env NEO4J_AUTH=neo4j/test neo4j
