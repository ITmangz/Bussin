import { BusFront } from "lucide-react";

import AppCard from "../components/ui/AppCard";

import "./Queue.css";

function Queue() {
  return (
    <section className="queue-page">
      <header className="queue-header">
        <div>
          <h1>Queue</h1>

          <p>Track your boarding queue and estimated waiting time.</p>
        </div>
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
            information is displayed until that data is available from the
            server.
          </p>
        </div>
      </AppCard>
    </section>
  );
}

export default Queue;
