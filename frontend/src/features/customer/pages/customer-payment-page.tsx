import { useEffect, useState } from "react";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import { Link } from "react-router-dom";
import { usePageLayoutContext } from "@/context/page-layout-context.ts";
import axios from "axios";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";

interface PayPalQrCodeResponse {
  paypalOrderId: string;
  qrCodeBase64: string;
  qrCodeDataUrl: string;
}

const CustomerPaymentPage = () => {
  const { setSidebar } = usePageLayoutContext();
  const [qrCode, setQrCode] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [paymentStatus, setPaymentStatus] = useState<"pending" | "processing" | "success" | "failed">("pending");

  const {customerSession, renewCustomerSession, removeCustomerSession, setRefreshInterval} = useCustomerSessionContext();

  // Remove the sidebar with the categories
  useEffect(() => {
    setSidebar(undefined);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    renewCustomerSession();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (customerSession?.order?.status === "PAID") {
      setPaymentStatus("success");
    } else if (customerSession?.order?.status === "PENDING") {
      setPaymentStatus("pending");
    } else if (customerSession?.order?.status === "APPROVING") {
      setPaymentStatus("processing");
    }
  }, [customerSession?.order?.status]);

  useEffect(() => {
    if (paymentStatus === "pending") {
      console.log("Payment pending");
      setRefreshInterval(1);
      return;
    }
    if (paymentStatus === "success") {
      console.log("Payment success");
      setRefreshInterval(15);
      return;
    }
    if (paymentStatus === "failed") {
      console.log("Payment failed");
      setRefreshInterval();
      setTimeout(() => {
        removeCustomerSession();
      }, 10000);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [customerSession, paymentStatus]);

  // Fetch QR code when component mounts
  useEffect(() => {
    const fetchQrCode = async () => {
      if (!customerSession?.order?.id) {
        setError("No order found");
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get<PayPalQrCodeResponse>(
          `/api/paypal/qr-code`
        );
        setQrCode(response.data.qrCodeDataUrl);
        setLoading(false);

      } catch (err) {
        console.error("Error fetching QR code:", err);
        setError("Failed to load payment QR code");
        setLoading(false);
      }
    };

    fetchQrCode();
  }, [customerSession?.order?.id]);

  return (
    <div className="p-4 text-gray-800 flex flex-col gap-2 h-full">
      <MinimalCard className="h-min">
        <h2 className="text-lg">Bezahlen mit PayPal</h2>
        <p className="italic">
          Scanne den QR-Code mit deiner PayPal App, um die Bestellung zu bezahlen.
        </p>
      </MinimalCard>

      <MinimalCard className="my-4 h-full flex flex-col items-center justify-center p-6">
        {loading && <p>Lade QR-Code...</p>}
        {error && <p className="text-red-500">{error}</p>}
        {paymentStatus}
        {qrCode && (
          <div className="flex flex-col items-center relative">
            <img src={qrCode} alt="PayPal QR Code" className="w-64 h-64" />
            {paymentStatus === "processing" && <>
              <div role="status" className="absolute -translate-x-1/2 -translate-y-1/2 top-2/4 left-1/2">
                <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/><path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/></svg>
                <span className="sr-only">Loading...</span>
              </div>
              <div className="my-4 p-3 bg-yellow-100 text-yellow-800 rounded-md">
                Zahlung wird bearbeitet! Warten auf Bestätigung.
              </div>
            </>}
            {paymentStatus === "success" && (
              <div className="my-4 p-3 bg-green-100 text-green-800 rounded-md">
                Zahlung erfolgreich! Deine Bestellung wird jetzt zubereitet.
              </div>
            )}
          </div>
        )}
      </MinimalCard>

      {customerSession?.order?.items?.length && (
        <MinimalCard className="my-4 grid w-full h-full grid-cols-1 md:grid-cols-2 lg:grid-cols-3 auto-rows-fr gap-3">
          <h2 className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 row-start-1 text-lg">
            Bestellung
          </h2>
          {customerSession.order.items.map((item) => (
            <dl
              key={item.id}
              className="row-span-1 col-span-1 text-sm border-t-1 mt-1 pt-1 border-gray-200 grid grid-rows-2 grid-cols-3 gap-0"
            >
              <dt className="row-span-2 col-start-1">{item.item?.name}</dt>
              <dd className="row-span-2 col-start-2 italic">
                {item.item?.descriptionForCart}
              </dd>
              <dd className="text-xs text-gray-400 text-right">
                {item.amount} x {item.item?.price}€
              </dd>
              <dd className="text-right">
                <span className="text-xs text-gray-400">=</span> {item.price}€
              </dd>
            </dl>
          ))}
          <dl className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 -row-start-0 self-end text-sm font-bold border-t-3 border-double mt-1 pt-1 border-gray-800 grid grid-rows-2 grid-cols-2 gap-0">
            <dt>Gesamt</dt>
            <dd className="text-right">{customerSession.order.totalPrice}€</dd>
          </dl>
        </MinimalCard>
      )}

      <Link to="/checkout" className="btn btn-neutral block text-center w-full mt-2">
        Zurück
      </Link>
    </div>
  );
};

export default CustomerPaymentPage;