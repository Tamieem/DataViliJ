package algorithms;

import org.junit.Assert;
import org.junit.Test;

public class AlgorithmConfigurationTest {

    // If max Iterations in cluster is negative
    @Test(expected = AlgorithmConfiguration.IllegalMaxIterationException.class)
    public void testClusterConfigurationsTest() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("-5", "3", "4");
    }

    // If update interval is negative or greater than max iterations in clusters
    @Test(expected = AlgorithmConfiguration.IllegalUpdateIntervalException.class)
    public void testClusterConfigurationsTest2() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("5", "3", "-4");
        AlgorithmConfiguration.testClusterConfigurations("5", "3", "7");
    }

    // If one of the cluster configuration values is left blank
    @Test(expected = NumberFormatException.class)
    public void testClusterConfigurationsTest3() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("", "3", "4");
        AlgorithmConfiguration.testClusterConfigurations("3", "", "4");
        AlgorithmConfiguration.testClusterConfigurations("3", "3", "");
    }

    // If a letter is entered into one of the configuration values for cluster
    @Test(expected = NumberFormatException.class)
    public void testClusterConfigurationsTest4() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("a", "3", "4");
        AlgorithmConfiguration.testClusterConfigurations("3", "b", "4");
        AlgorithmConfiguration.testClusterConfigurations("3", "3", "c");
    }

    // Passed Test
    @Test
    public void testClusterConfigurationsTest5() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        Assert.assertEquals(AlgorithmConfiguration.testClusterConfigurations("5", "3", "4"), true);

    }

    // Similar tests for classification values


    @Test(expected = AlgorithmConfiguration.IllegalMaxIterationException.class)
    public void testClassificationConfigurationsTest() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("-5", "3", "4");
    }
    @Test(expected = AlgorithmConfiguration.IllegalUpdateIntervalException.class)
    public void testClassicationConfigurationsTest2() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClusterConfigurations("5", "3", "-4");
        AlgorithmConfiguration.testClusterConfigurations("5", "3", "7");
    }
    @Test(expected = NumberFormatException.class)
    public void testClassificationConfigurationsTest3() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClassificationConfigurations("", "3");
        AlgorithmConfiguration.testClassificationConfigurations("3", "");
    }
    @Test(expected = NumberFormatException.class)
    public void testClassificationConfigurationsTest4() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        AlgorithmConfiguration.testClassificationConfigurations("a", "3");
        AlgorithmConfiguration.testClassificationConfigurations("3", "b");
    }
    // Passed Test
    @Test
    public void testClassificationConfigurationsTest5() throws AlgorithmConfiguration.IllegalUpdateIntervalException, AlgorithmConfiguration.IllegalMaxIterationException {
        Assert.assertEquals(AlgorithmConfiguration.testClassificationConfigurations("5", "3"), true);

    }

}