import { HashRouter, Routes, Route } from 'react-router-dom'
import { lazy, Suspense } from 'react'

const BarcodePage = lazy(() => import('./pages/BarcodePage'))
const LandingPage = lazy(() => import('./pages/LandingPage'))
const DetailPage = lazy(() => import('./pages/DetailPage'))
const SubmitPage = lazy(() => import('./pages/SubmitPage'))
const SuccessPage = lazy(() => import('./pages/SuccessPage'))
const ExportPage = lazy(() => import('./pages/ExportPage'))

function App() {
  return (
    <HashRouter>
      <div className="master">
        <Suspense fallback={<div>加载中...</div>}>
          <Routes>
            <Route path="/" element={<BarcodePage />} />
            <Route path="/index" element={<LandingPage />} />
            <Route path="/detail" element={<DetailPage />} />
            <Route path="/submit" element={<SubmitPage />} />
            <Route path="/sucess" element={<SuccessPage />} />
            <Route path="/export" element={<ExportPage />} />
          </Routes>
        </Suspense>
      </div>
    </HashRouter>
  )
}

export default App
