export function printBookingDocument(event, bodyClassName) {
  const widget = event.currentTarget.closest("[data-print-document]");
  const documentElement = widget?.querySelector("[data-print-target]");

  if (!documentElement) return;

  const body = document.body;
  let timeoutId;
  const finishPrinting = () => {
    window.clearTimeout(timeoutId);
    body.classList.remove(bodyClassName);
    documentElement.removeAttribute("data-print-active");
    window.removeEventListener("afterprint", finishPrinting);
  };

  documentElement.setAttribute("data-print-active", "true");
  body.classList.add(bodyClassName);
  window.addEventListener("afterprint", finishPrinting, { once: true });
  timeoutId = window.setTimeout(finishPrinting, 120000);

  window.print();
}
