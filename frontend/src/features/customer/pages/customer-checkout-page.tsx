import MinimalCard from "@/components/shared/minimal-card.tsx";
import {Link, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import axios from "axios";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";

const CustomerCheckoutPage = () => {
    const {setSidebar} = usePageLayoutContext();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const {customerSession} = useCustomerSessionContext();

    // Remove the sidebar with the categories
    useEffect(() => {
        setSidebar(undefined);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Function to place the order and navigate to payment page
    const handlePlaceOrder = async () => {
        if (!customerSession?.order) {
            setError("No order found");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            // Call the API to place the order
            await axios.post('/api/orders', customerSession.order);

            // Navigate to payment page
            navigate('/checkout/payment');
        } catch (err) {
            console.error("Error placing order:", err);
            setError("Failed to place order. Please try again.");
            setLoading(false);
        }
    };

    return <div className="p-4 text-gray-800 flex flex-col gap-2 h-full">
        <MinimalCard className="h-min">
            <h2 className="text-lg">Bezahlen</h2>
            <p className="italic">Nach dem Bezahlen wird die Bestellung zubereitet. Merke dir die Nummer und verfolge
                den Fortschritt auf dem Bildschirm über der Kasse!</p>
        </ MinimalCard>
        {!!customerSession?.order?.items?.length && <MinimalCard className="my-4 grid w-full h-full grid-cols-1 md:grid-cols-2 lg:grid-cols-3 auto-rows-fr gap-3" >
            <h2 className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 row-start-1 text-lg">Bestellung</h2>
            {customerSession.order.items.map(item =>
                <dl key={item.id}
                    className="row-span-1 col-span-1 text-sm border-t-1 mt-1 pt-1 border-gray-200 grid grid-rows-2 grid-cols-3 gap-0">
                    <dt className="row-span-2 col-start-1">{item.item?.name}</dt>
                    <dd className="row-span-2 col-start-2 italic">{item.item?.descriptionForCart}</dd>
                    <dd className="text-xs text-gray-400 text-right">{item.amount} x {item.item?.price}€</dd>
                    <dd className="text-right"><span className="text-xs text-gray-400">=</span> {item.price}€</dd>
                </dl>)}
            <dl className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 -row-start-0 self-end text-sm font-bold border-t-3 border-double mt-1 pt-1 border-gray-800 grid grid-rows-2 grid-cols-2 gap-0">
                <dt>Gesamt</dt>
                <dd className="text-right">{customerSession.order.totalPrice}€</dd>
            </dl>
        </MinimalCard>}

        {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-2" role="alert">
                <span className="block sm:inline">{error}</span>
            </div>
        )}

        <button 
            onClick={handlePlaceOrder} 
            disabled={loading || !customerSession?.order?.items?.length} 
            className="btn btn-primary block text-center w-full mt-2"
        >
            {loading ? 'Bestellung wird aufgegeben...' : 'Bestellung bezahlen'}
        </button>

        <Link to="/" className="btn btn-neutral block text-center w-full mt-2">Zurück und Bestellung fortsetzen</Link>
    </div>
}

export default CustomerCheckoutPage;
