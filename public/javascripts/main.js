const prop = (key) => (obj) => obj[key];
const map = (f) => (arr) => arr.map(f);

const remainingTimeHours = (data, now) =>
  map((item) => (new Date(item.auctionEndTimestamp) - now) / 1000 / 3600)(data);

const markerColor = (time) => (time <= 0 ? 'red' : 'blue');
const markerSize = (minTime, maxTime, time) => {
  const minSize = 10;
  const maxSize = 200;
  const endedMarkerSize = 12;
  return time <= 0
    ? endedMarkerSize
    : minSize + (maxSize - minSize) * ((time - minTime) / (maxTime - minTime));
};

const createPlot = (data) => {
  const now = new Date();
  const hoursRemaining = remainingTimeHours(data, now);
  const minRemainingTime = Math.min(...hoursRemaining);
  const maxRemainingTime = Math.max(...hoursRemaining);

  Plotly.newPlot(
    'chart',
    [
      {
        x: map(prop('memorabilityScore'))(data),
        y: map(prop('minimumBidInUSD'))(data),
        text: map(prop('number'))(data),
        mode: 'markers',
        marker: {
          size: map((time) =>
            markerSize(minRemainingTime, maxRemainingTime, time)
          )(hoursRemaining),
          color: map(markerColor)(hoursRemaining),
          sizemode: 'diameter',
        },
        type: 'scatter',
      },
    ],
    {
      title: 'Memorability Score vs Price (Size represents time left)',
      xaxis: { title: 'Memorability Score' },
      yaxis: { title: 'Price (USD)', type: 'log' },
      showlegend: false,
      hovermode: 'closest',
    }
  );
};

const onWebSocketMessage = (event) => createPlot(JSON.parse(event.data));

const mode = new URLSearchParams(window.location.search).get('mode');
function getAPIEndpointByMode(mode) {
  return (
    {
      future: 'future-auctions',
      past: 'past-auctions',
    }[mode] || 'auctions'
  );
}

//
const webSocket = new WebSocket(
  `wss://fragmenty.cloud/ws/${getAPIEndpointByMode(mode)}`
);
webSocket.addEventListener('message', onWebSocketMessage);
