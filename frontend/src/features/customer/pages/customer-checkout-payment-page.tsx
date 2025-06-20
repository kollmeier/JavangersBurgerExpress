import React, {useEffect, useRef, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import axios from "axios";
import BeButton from "@/components/ui/be-button.tsx";
import BeButtonLink from "@/components/ui/be-button-link.tsx";

interface StripePaymentQrCodeResponse {
    stripePaymentOrderId: string;
    qrCodeBase64: string;
    qrCodeDataUrl: string;
}

export type CustomerCheckoutPaymentPageProps = {
    setButtons: (buttons: React.JSX.Element) => void;
}

export const CustomerCheckoutPaymentPage: React.FC<CustomerCheckoutPaymentPageProps> = ({setButtons}) => {
    const [qrCode, setQrCode] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const {process, provider} = useParams()
    const navigate = useNavigate();

    const cancelQr = useRef<AbortController | null>(null);

    useEffect(() => {
        renewCustomerSession()
        if (process === "success") {
            setLoading(false);
            navigate("/checkout/" + provider + "/payment/process/success");
            return;
        }
        if (process === "waiting") {
            setButtons(<BeButton onClick={() => removeCustomerSession()}>Bestellung schließen</BeButton>)
            setLoading(false);
            return;
        }
        setButtons(<>
            <BeButtonLink to="/checkout" className="text-center w-full">Zurück und Bestellung ändern</BeButtonLink>
            <BeButton className="text-center w-full" onClick={() => removeCustomerSession()}>Bestellung abbrechen</BeButton>
        </>)
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [process, navigate]);

    const {
        customerSession,
        renewCustomerSession,
        removeCustomerSession,
        setRefreshInterval
    } = useCustomerSessionContext();

    useEffect(() => {
        renewCustomerSession();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (customerSession?.order?.status === "PAID") {
            navigate("/checkout/" + provider + "/payment/process/success");
        } else if (customerSession?.order?.status === "PENDING") {
            navigate("/checkout/" + provider + "/payment/");
        } else if (customerSession?.order?.status === "APPROVING") {
            navigate("/checkout/" + provider + "/payment/process/waiting");
        }
    }, [customerSession?.order?.status, navigate, provider]);

    useEffect(() => {
        if (!process || process === "waiting") {
            setRefreshInterval(1);
            return;
        }
        if (process === "failed") {
            setRefreshInterval();
            setTimeout(() => {
                removeCustomerSession();
            }, 10000);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [process]);

    // Fetch QR code when component mounts
    useEffect(() => {
        const fetchQrCode = async () => {
            if (!customerSession?.order?.id) {
                setError("No order found");
                setLoading(false);
                return;
            }

            cancelQr.current?.abort();
            cancelQr.current = new AbortController();

            try {
                const response = await axios.get<StripePaymentQrCodeResponse>(
                    `/api/` + provider + `/qr-code`,
                    {
                        signal: cancelQr.current.signal,
                    }
                );
                setQrCode(response.data.qrCodeDataUrl);
                setLoading(false);

            } catch (err) {
                if (axios.isCancel(err)) {
                    return;
                }
                console.error("Error fetching QR code:", err);
                setError("Failed to load payment QR code");
                setLoading(false);
            }
        };

        fetchQrCode();
    }, [customerSession?.order?.id, provider]);

    return (<>
        {process === "pending" && <p className="italic">
            Scanne den QR-Code mit deinem Mobilgerät, um die Bestellung zu bezahlen.
        </p>}
        {process === "waiting" && <p className="italic">
            Folge den Anweisungen auf deinem Mobilgerät. Sobald deine Zahlung erfolgreich ist, geht es hier weiter. (Das kann manchmal einige Minuten dauern.)
        </p>}
        <div className="my-4 h-full flex flex-col items-center justify-center p-6">
            {loading && <p>Lade QR-Code...</p>}
            {error && <p className="text-red-500">{error}</p>}
            {qrCode && (
                <div className="flex flex-col items-center relative">
                    <img src={qrCode} alt="PayPal QR Code" className="w-64 h-64"/>
                    {process === "waiting" && <>
                        <output
                            className="absolute flex items-center justify-center w-64 h-64 -translate-x-1/2 top-0 left-1/2 bg-white/80">
                            <svg aria-hidden="true"
                                 className="w-34 h-34 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600"
                                 viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path
                                    d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
                                    fill="currentColor"/>
                                <path
                                    d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
                                    fill="currentFill"/>
                            </svg>
                            <span className="sr-only">Loading...</span>
                        </output>
                        <div className="my-4 p-3 bg-yellow-100 text-yellow-800 rounded-md">
                            Zahlung wird bearbeitet! Warten auf Bestätigung.
                        </div>
                    </>}
                </div>
            )}
        </div>
    </>);
}