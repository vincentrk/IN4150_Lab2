#!/bin/bash
numOfProcces=$1
numOfIterations=$2
echo "Starting Script for " $numOfProcces" Proccess and " $numOfIterations "Number of Iterations"
for ((i=0;i<numOfProcces;i++));
do
	java Main $i $numOfProcces $numOfIterations &
done
