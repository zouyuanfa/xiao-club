import { useLocation } from 'react-router-dom'

const SuccessPage = () => {
  const location = useLocation()
  const query = new URLSearchParams(location.search)
  const number = query.get('number')
  const name = query.get('name')

  return (
    <div className="imgcommon success">
      <div className="success-content">
        <div className="success-message">
          <div className="success-name">
            <span>{name}</span>
            先生/女士
          </div>
          <div>恭喜你成为棠·CLUB会员</div>
        </div>
        <div className="success-number">
          <span>您的会员号</span>
          {number}
        </div>
      </div>
    </div>
  )
}

export default SuccessPage
