import { useState, useEffect, useRef } from 'react'
import { QRCodeSVG } from 'qrcode.react'

const qrValue = `${window.location.href}#/index`

const BarcodePage = () => {
  const [active, setActive] = useState(false)
  const timerRef = useRef(null)

  const isExpired = () => {
    const expireTime = localStorage.getItem('qrCodeExpireTime')
    const now = new Date().getTime()

    if (!expireTime || now > Number(expireTime)) {
      setActive(false)
      clearInterval(timerRef.current)
      return true
    }
    return false
  }

  const startPolling = () => {
    timerRef.current = setInterval(() => {
      console.log('checkQRCodeExpiration')
      isExpired()
    }, 3000)
  }

  useEffect(() => {
    const expireAt = new Date().getTime() + 30000
    localStorage.setItem('qrCodeExpireTime', expireAt.toString())
    setActive(true)
    startPolling()

    return () => {
      clearInterval(timerRef.current)
    }
  }, [])

  const refreshQRCode = () => {
    const expireAt = new Date().getTime() + 30000
    localStorage.setItem('qrCodeExpireTime', expireAt.toString())
    setActive(true)
    startPolling()
  }

  return (
    <div className="barcode">
      <div className="qrcode">
        <QRCodeSVG
          value={qrValue}
          size={200}
          level="Q"
          bgColor="#ffffff"
          fgColor="#000000"
        />
        {!active && <div className="mask" onClick={refreshQRCode} />}
      </div>
      {!active && (
        <div className="refresh">
          <p>二维码失效，点击刷新</p>
        </div>
      )}
    </div>
  )
}

export default BarcodePage
