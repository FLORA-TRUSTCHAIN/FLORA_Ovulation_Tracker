import React, { useMemo } from 'react';
import {
  Box,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableCaption,
  TableContainer,
  Heading,
  Button,
  Input,
  useColorMode,
} from '@chakra-ui/react';
import ReactApexChart from 'react-apexcharts';
import { useTable, useFilters, usePagination } from 'react-table';

const sampleData = [
  { name: 'Page A', uv: 4000, pv: 2400, amt: 2400 },
  { name: 'Page B', uv: 3000, pv: 1398, amt: 2210 },
  { name: 'Page C', uv: 2000, pv: 9800, amt: 2290 },
  { name: 'Page D', uv: 2780, pv: 3908, amt: 2000 },
  { name: 'Page E', uv: 1890, pv: 4800, amt: 2181 },
  { name: 'Page F', uv: 2390, pv: 3800, amt: 2500 },
  { name: 'Page G', uv: 3490, pv: 4300, amt: 2100 },
  // Add more data as needed
];

const calculateBoxPlotData = (data) => {
  const sortedData = data.slice().sort((a, b) => a - b);
  const min = sortedData[0];
  const max = sortedData[sortedData.length - 1];
  const median = sortedData[Math.floor(sortedData.length / 2)];
  const q1 = sortedData[Math.floor(sortedData.length / 4)];
  const q3 = sortedData[Math.floor(sortedData.length * 3 / 4)];

  return [min, q1, median, q3, max];
};

const uvData = sampleData.map(item => item.uv);
const pvData = sampleData.map(item => item.pv);
const amtData = sampleData.map(item => item.amt);

const uvBoxPlotData = calculateBoxPlotData(uvData);
const pvBoxPlotData = calculateBoxPlotData(pvData);
const amtBoxPlotData = calculateBoxPlotData(amtData);

function Filter({ column }) {
  return (
    <Input
      value={column.filterValue || ''}
      onChange={e => column.setFilter(e.target.value || undefined)}
      placeholder={`Search ${column.id}`}
      size="sm"
      my={1}
    />
  );
}

export default function Charts() {
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
        name: 'UV',
        data: sampleData.map(item => item.uv),
      },
      {
        name: 'PV',
        data: sampleData.map(item => item.pv),
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      categories: sampleData.map(item => item.name),
    },
    colors: ['#00E396', '#0000FF'],
  };

  const seriesChartOptions = {
    ...commonOptions,
    chart: {
      ...commonOptions.chart,
      type: 'line',
    },
    series: [
      {
        name: 'UV',
        data: sampleData.map(item => item.uv),
        color: '#00E396'
      },
      {
        name: 'PV',
        data: sampleData.map(item => item.pv),
        color: '#0000FF'
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      categories: sampleData.map(item => item.name),
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
        name: 'Sample Data',
        data: sampleData.map(item => [item.uv, item.pv]),
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      title: {
        text: 'UV'
      },
    },
    yaxis: {
      ...commonOptions.yaxis,
      title: {
        text: 'PV'
      },
    },
    markers: {
      size: 5,
      strokeColors: isDark ? '#1A202C' : '#fff',
      strokeWidth: 2,
    },
  };

  const boxPlotOptions = {
    ...commonOptions,
    chart: {
      ...commonOptions.chart,
      type: 'boxPlot',
    },
    series: [
      {
        name: 'Box',
        data: [
          {
            x: 'UV',
            y: uvBoxPlotData,
          },
          {
            x: 'PV',
            y: pvBoxPlotData,
          },
          {
            x: 'AMT',
            y: amtBoxPlotData,
          },
        ],
      },
    ],
    xaxis: {
      ...commonOptions.xaxis,
      categories: ['UV', 'PV', 'AMT'],
      tickPlacement: 'on',
    },
    yaxis: {
      ...commonOptions.yaxis,
      title: {
        text: 'Values'
      }
    },
    grid: {
      ...commonOptions.grid,
      padding: {
        left: 10,
        right: 10
      }
    },
    stroke: {
      width: 1,
    },
    tooltip: {
      shared: false,
      intersect: true
    }
  };

  const data = useMemo(() => sampleData, []);
  const columns = useMemo(() => [
    {
      Header: 'Name',
      accessor: 'name',
      Filter: Filter,
    },
    {
      Header: 'UV',
      accessor: 'uv',
      isNumeric: true,
      Filter: Filter,
    },
    {
      Header: 'PV',
      accessor: 'pv',
      isNumeric: true,
      Filter: Filter,
    },
    {
      Header: 'AMT',
      accessor: 'amt',
      isNumeric: true,
      Filter: Filter,
    },
  ], []);

  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    page,
    prepareRow,
    state: { pageIndex, pageSize },
    canPreviousPage,
    canNextPage,
    pageOptions,
    gotoPage,
    nextPage,
    previousPage,
    setPageSize,
  } = useTable(
    {
      columns,
      data,
      initialState: { pageIndex: 0, pageSize: 5 }, // Set initial page size
    },
    useFilters,
    usePagination
  );
  

  return (
    <Box p={4}>
      <TableContainer w="75%" mx="auto">
        <Table {...getTableProps()} variant="striped" size='sm'>
          <TableCaption>Data Table</TableCaption>
          <Thead>
            {headerGroups.map(headerGroup => (
              <Tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map(column => (
                  <Th {...column.getHeaderProps()}>
                    {column.render('Header')}
                    <div>{column.canFilter ? column.render('Filter') : null}</div>
                  </Th>
                ))}
              </Tr>
            ))}
          </Thead>
          <Tbody {...getTableBodyProps()}>
            {page.map(row => {
              prepareRow(row);
              return (
                <Tr {...row.getRowProps()}>
                  {row.cells.map(cell => (
                    <Td {...cell.getCellProps()} isNumeric={cell.column.isNumeric}>
                      {cell.render('Cell')}
                    </Td>
                  ))}
                </Tr>
              );
            })}
          </Tbody>
        </Table>
      </TableContainer>
      <Box display="flex" justifyContent="space-between" mt={4} alignItems="center">
  <Button onClick={() => gotoPage(0)} disabled={!canPreviousPage} size="sm" mr={2}>
    {'<<'}
  </Button>
  <Button onClick={() => previousPage()} disabled={!canPreviousPage} size="sm" mr={2}>
    {'<'}
  </Button>
  <Button onClick={() => nextPage()} disabled={!canNextPage} size="sm" mr={2}>
    {'>'}
  </Button>
  <Button onClick={() => gotoPage(pageOptions.length - 1)} disabled={!canNextPage} size="sm">
    {'>>'}
  </Button>
  <Box>
    Page{' '}
    <strong>
      {pageIndex + 1} of {pageOptions.length}
    </strong>
  </Box>
  <Box>
    | Go to page:{' '}
    <Input
      type="number"
      defaultValue={pageIndex + 1}
      onChange={(e) => {
        const page = e.target.value ? Number(e.target.value) - 1 : 0;
        gotoPage(page);
      }}
      style={{ width: '100px' }}
      size="sm"
      ml={2}
    />
  </Box>
  <Box>
    <select
      value={pageSize}
      onChange={(e) => {
        setPageSize(Number(e.target.value));
      }}
      style={{ width: '120px' }}
      ml={2}
    >
      {[5, 10, 20, 30, 40, 50].map(pageSize => (
        <option key={pageSize} value={pageSize}>
          Show {pageSize}
        </option>
      ))}
    </select>
  </Box>
</Box>


      <Box mt={8}>
        <Heading size="md" mb={4}>Apex Bar Chart</Heading>
        <ReactApexChart
          options={barChartOptions}
          series={barChartOptions.series}
          type="bar"
          height={300}
        />
      </Box>

      <Box mt={8}>
        <Heading size="md" mb={4}>Apex Line Chart</Heading>
        <ReactApexChart
          options={seriesChartOptions}
          series={seriesChartOptions.series}
          type="line"
          height={300}
        />
      </Box>

      <Box mt={8}>
        <Heading size="md" mb={4}>Apex Scatter Chart</Heading>
        <ReactApexChart
          options={scatterChartOptions}
          series={scatterChartOptions.series}
          type="scatter"
          height={300}
        />
      </Box>

      <Box mt={8}>
        <Heading size="md" mb={4}>Apex Box Plot</Heading>
        <ReactApexChart
          options={boxPlotOptions}
          series={boxPlotOptions.series}
          type="boxPlot"
          height={300}
        />
      </Box>
    </Box>
  );
}
