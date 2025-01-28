import React, { useState } from 'react';
import ReactApexChart from 'react-apexcharts';
import {
  Box,
  Heading,
  Select,
  Button,
  useColorMode,
} from '@chakra-ui/react';

const ChartTemplate = ({ data, chartType }) => {
  const [selectedFeature1, setSelectedFeature1] = useState('feature1');
  const [selectedFeature2, setSelectedFeature2] = useState('feature3');
  const [showChart, setShowChart] = useState(true);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day}-${hours}:${minutes}:${seconds}`;
  };

  const feature1Data = data.map(item => item.feature1);
  const feature2Data = data.map(item => item.feature2);
  const feature3Data = data.map(item => item.feature3);
  const feature4Data = data.map(item => item.feature4);
  const featureDate = data.map(item => formatDate(item.date)); // Format dates to YYYY-MM-DD-HH:MM:SS

  const { colorMode } = useColorMode();
  const isDark = colorMode === 'dark';

  const commonOptions = {
    chart: {
      background: isDark ? '#1A202C' : '#fff',
      foreColor: isDark ? '#f0f0f0' : '#333',
    },
    theme: {
      mode: isDark ? 'dark' : 'light'
    },
    xaxis: {
      labels: {
        style: {
          colors: isDark ? '#f0f0f0' : '#333'
        }
      }
    },
    yaxis: {
      labels: {
        style: {
          colors: isDark ? '#f0f0f0' : '#333'
        }
      }
    },
    grid: {
      borderColor: isDark ? '#444' : '#e0e0e0'
    },
  };

  const barChartOptions = {
    ...commonOptions,
    chart: {
      ...commonOptions.chart,
      type: 'bar',
    },
    series: [
      {
        name: 'Feature 1',
        data: feature1Data,
      },
      {
        name: 'Feature 2',
        data: feature2Data,
      },
      {
        name: 'Feature 3',
        data: feature3Data,
      },
      {
        name: 'Feature 4',
        data: feature4Data,
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      categories: featureDate,
    },
    colors: ['#00E396', '#0000FF'],
  };

  const lineChartOptions = {
    ...commonOptions,
    chart: {
      ...commonOptions.chart,
      type: 'line',
    },
    series: [
      {
        name: 'Feature 1',
        data: feature1Data,
        color: '#00E396'
      },
      {
        name: 'Feature 2',
        data: feature2Data,
        color: '#0000FF'
      },
      {
        name: 'Feature 3',
        data: feature3Data,
        color: '#0000FF'
      },
      {
        name: 'Feature 4',
        data: feature4Data,
        color: '#0000FF'
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      categories: featureDate,
    },
    markers: {
      size: 5,
      colors: ['#00E396', '#0000FF'],
      strokeColors: isDark ? '#1A202C' : '#fff',
      strokeWidth: 2,
    },
  };

  const scatterChartOptions = {
    ...commonOptions,
    chart: {
      ...commonOptions.chart,
      type: 'scatter',
      zoom: {
        enabled: true,
        type: 'xy'
      }
    },
    series: [
      {
        name: `${selectedFeature1} vs ${selectedFeature2}`,
        data: data.map(item => [item[selectedFeature1], item[selectedFeature2]]),
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      title: {
        text: selectedFeature1
      },
    },
    yaxis: {
      ...commonOptions.yaxis,
      title: {
        text: selectedFeature2
      },
    },
    markers: {
      size: 5,
      strokeColors: isDark ? '#1A202C' : '#fff',
      strokeWidth: 2,
    },
  };

  const chartOptions = {
    line: lineChartOptions,
    bar: barChartOptions,
    scatter: scatterChartOptions,
  };

  const handleFeature1Change = (e) => {
    setSelectedFeature1(e.target.value);
  };

  const handleFeature2Change = (e) => {
    setSelectedFeature2(e.target.value);
  };

  const handleRegenerate = () => {
    setShowChart(false); // Hide the chart
    setTimeout(() => setShowChart(true), 100); // Show the chart again after a short delay
  };

  return (
    <Box mt={8} width="100%">
      <Heading size="md" mb={4}>Apex {chartType.charAt(0).toUpperCase() + chartType.slice(1)} Chart</Heading>
      
      {chartType === 'scatter' && (
        <Box mb={4}>
          <Select value={selectedFeature1} onChange={handleFeature1Change} mr={2}>
            <option value="feature1">Feature 1</option>
            <option value="feature2">Feature 2</option>
            <option value="feature3">Feature 3</option>
            <option value="feature4">Feature 4</option>
          </Select>
          <Select value={selectedFeature2} onChange={handleFeature2Change} mr={2}>
            <option value="feature1">Feature 1</option>
            <option value="feature2">Feature 2</option>
            <option value="feature3">Feature 3</option>
            <option value="feature4">Feature 4</option>
          </Select>
          <Button onClick={handleRegenerate}>Regenerate</Button>
        </Box>
      )}

      {showChart && (
        <ReactApexChart
          options={chartOptions[chartType]}
          series={chartOptions[chartType].series}
          type={chartType}
          height={500}
          width="100%"
        />
      )}
    </Box>
  );
};

export default ChartTemplate;
