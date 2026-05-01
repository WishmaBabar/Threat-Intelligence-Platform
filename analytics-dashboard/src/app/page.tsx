"use client";

import { useEffect, useState } from "react";
import { Shield, Activity, Database, AlertTriangle, Search, RefreshCw, BarChart3, Globe } from "lucide-react";
import axios from "axios";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from "recharts";

const DB_API = "http://127.0.0.1:8088/api";

export default function Dashboard() {
  const [iocs, setIocs] = useState<any[]>([]);
  const [summary, setSummary] = useState<any>({});
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchData = async () => {
    try {
      const timestamp = new Date().getTime();
      const [iocsRes, summaryRes] = await Promise.all([
        axios.get(`${DB_API}/iocs?t=${timestamp}`),
        axios.get(`${DB_API}/analytics/summary?t=${timestamp}`)
      ]);
      setIocs(iocsRes.data.sort((a: any, b: any) => b.id - a.id));
      setSummary(summaryRes.data);
    } catch (error) {
      console.error("Error fetching data", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = async () => {
    setRefreshing(true);
    await Promise.all([
      fetchData(),
      new Promise(resolve => setTimeout(resolve, 600)) // minimum 600ms spin
    ]);
    setRefreshing(false);
  };

  const chartData = Object.entries(summary).map(([name, value]) => ({ name, value }));

  return (
    <div className="min-h-screen p-8 bg-[#050505] text-white">
      {/* Header */}
      <div className="flex justify-between items-center mb-12">
        <div>
          <h1 className="text-4xl font-bold tracking-tight mb-2 bg-gradient-to-r from-blue-500 to-emerald-500 bg-clip-text text-transparent">
            Threat Intelligence Platform
          </h1>
          <p className="text-gray-400 font-medium flex items-center gap-2">
            <Activity className="w-4 h-4 text-emerald-500 animate-pulse" />
            Distributed Microservices Monitoring System
          </p>
        </div>
        <button 
          onClick={handleRefresh}
          className={`glass-card px-6 py-3 flex items-center gap-2 font-semibold hover:bg-white/10 transition-all ${refreshing ? 'opacity-50' : ''}`}
        >
          <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
          Refresh Data
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
        <StatCard title="Total IOCs" value={iocs.length} icon={<Database className="text-blue-500" />} color="blue" />
        <StatCard title="IP Addresses" value={summary.ip || 0} icon={<Globe className="text-emerald-500" />} color="green" />
        <StatCard title="Domain Names" value={summary.domain || 0} icon={<Search className="text-orange-500" />} color="orange" />
        <StatCard title="High Severity" value={iocs.filter(i => i.severity > 7).length} icon={<AlertTriangle className="text-red-500" />} color="red" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Chart Section */}
        <div className="lg:col-span-1 glass-card p-6">
          <h2 className="text-xl font-bold mb-6 flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-blue-500" />
            Distribution by Type
          </h2>
          <div className="h-[300px] w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#222" />
                <XAxis dataKey="name" stroke="#666" />
                <YAxis stroke="#666" />
                <Tooltip 
                  contentStyle={{ background: '#121212', border: '1px solid #333' }}
                  itemStyle={{ color: '#fff' }}
                />
                <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={index === 0 ? '#3b82f6' : '#10b981'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Table Section */}
        <div className="lg:col-span-2 glass-card p-6 overflow-hidden">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-bold flex items-center gap-2">
              <Shield className="w-5 h-5 text-emerald-500" />
              Latest Threats
            </h2>
            <span className="text-xs font-mono text-gray-500 uppercase tracking-widest">Real-time Stream</span>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="text-left text-gray-500 border-b border-white/5">
                  <th className="pb-4 font-semibold uppercase text-[10px] tracking-wider">Type</th>
                  <th className="pb-4 font-semibold uppercase text-[10px] tracking-wider">IOC Value</th>
                  <th className="pb-4 font-semibold uppercase text-[10px] tracking-wider">Severity</th>
                  <th className="pb-4 font-semibold uppercase text-[10px] tracking-wider text-right">Detected At</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {iocs.slice(0, 10).map((ioc) => (
                  <tr key={ioc.id} className="group hover:bg-white/[0.02] transition-colors">
                    <td className="py-4">
                      <span className={`px-2 py-1 rounded text-[10px] font-bold uppercase ${
                        ioc.type === 'ip' ? 'bg-blue-500/10 text-blue-500' : 'bg-emerald-500/10 text-emerald-500'
                      }`}>
                        {ioc.type}
                      </span>
                    </td>
                    <td className="py-4 font-mono text-sm text-gray-300">{ioc.value}</td>
                    <td className="py-4">
                      <div className="flex items-center gap-2">
                        <div className="w-24 h-1.5 bg-gray-800 rounded-full overflow-hidden">
                          <div 
                            className={`h-full rounded-full ${getSeverityColor(ioc.severity)}`} 
                            style={{ width: `${ioc.severity * 10}%` }}
                          />
                        </div>
                        <span className="text-xs font-bold text-gray-400">{ioc.severity.toFixed(1)}</span>
                      </div>
                    </td>
                    <td className="py-4 text-right text-xs text-gray-500 font-mono">
                      {new Date(ioc.createdAt).toLocaleTimeString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, icon, color }: any) {
  const glowClass = `glow-${color}`;
  return (
    <div className={`glass-card p-6 ${glowClass}`}>
      <div className="flex justify-between items-start mb-4">
        <div className="p-2 bg-white/5 rounded-lg">{icon}</div>
        <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
      </div>
      <h3 className="text-gray-400 text-sm font-medium mb-1">{title}</h3>
      <p className="text-3xl font-bold tracking-tight">{value}</p>
    </div>
  );
}

function getSeverityColor(score: number) {
  if (score > 7) return 'bg-red-500';
  if (score > 4) return 'bg-orange-500';
  return 'bg-blue-500';
}
