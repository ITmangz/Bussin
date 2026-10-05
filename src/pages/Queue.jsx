import { BusFront, RefreshCw, Users } from "lucide-react";
import { useState } from "react";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";

import "./Queue.css";

function Queue() {
  const [refreshing, setRefreshing] = useState(false);

  function refreshQueue() {
    setRefreshing(true);

    window.setTimeout(() => {
      setRefreshing(false);
    }, 300);
  }

  return (
    <section className="queue-page">
      <header className="queue-header">
        <div>
          <h1>Queue</h1>

          <p>Track your boarding queue and estimated waiting time.</p>
        </div>

        <AppButton
          variant="secondary"
          onClick={refreshQueue}
          disabled={refreshing}
          className="queue-refresh-button"
        >
          <RefreshCw
            size={15}
            className={refreshing ? "queue-refreshing" : ""}
          />
          Refresh
        </AppButton>
      </header>

      <AppCard className="queue-instructions">
        <div className="queue-instructions-icon">
          <BusFront size={19} />
        </div>

        <div>
          <h3>Live queue data is not available yet</h3>

          <p>
            The current backend does not expose a passenger queue status
            endpoint. No queue position, waiting time, boarding state, or trip
            information is being displayed until that data is available from
            the server.
          </p>

          <div style={{ marginTop: "1rem" }}>
            <Users size={16} />
          </div>
        </div>
      </AppCard>
    </section>
  );
}

export default Queue;
