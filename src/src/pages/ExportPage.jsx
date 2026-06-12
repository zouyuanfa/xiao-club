import { useState } from 'react'
import axios from 'axios'

const getDownloadFilename = (contentDisposition) => {
  const match = contentDisposition?.match(/filename="?([^";]+)"?/)
  return match?.[1] || 'xiao-club-surveys.xlsx'
}

const ExportPage = () => {
  const [token, setToken] = useState('')
  const [status, setStatus] = useState('')
  const [isDownloading, setIsDownloading] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!token.trim()) {
      setStatus('请输入导出密码')
      return
    }

    setIsDownloading(true)
    setStatus('')

    try {
      const response = await axios.get('/api/survey/export', {
        headers: { 'X-Export-Token': token.trim() },
        responseType: 'blob',
      })
      const url = URL.createObjectURL(response.data)
      const link = document.createElement('a')
      link.href = url
      link.download = getDownloadFilename(response.headers['content-disposition'])
      document.body.appendChild(link)
      link.click()
      link.remove()
      URL.revokeObjectURL(url)
      setStatus('Excel 已开始下载')
    } catch (error) {
      setStatus(
        error.response?.status === 401
          ? '导出密码不正确'
          : '导出失败，请稍后重试'
      )
      console.error(error)
    } finally {
      setIsDownloading(false)
    }
  }

  return (
    <main className="export-page">
      <form className="export-panel" onSubmit={handleSubmit}>
        <h1>问卷数据导出</h1>
        <label htmlFor="export-token">导出密码</label>
        <input
          id="export-token"
          type="password"
          value={token}
          onChange={(event) => setToken(event.target.value)}
          placeholder="请输入导出密码"
          autoComplete="current-password"
          disabled={isDownloading}
        />
        <button type="submit" disabled={isDownloading}>
          {isDownloading ? '正在生成 Excel...' : '下载 Excel'}
        </button>
        <p className="export-status" role="status">
          {status}
        </p>
      </form>
    </main>
  )
}

export default ExportPage
