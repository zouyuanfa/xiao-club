import { useNavigate } from 'react-router-dom'
import SwiperPage from '../components/SwiperPage'

const LandingPage = () => {
  const navigate = useNavigate()

  const goDetail = () => {
    navigate('/detail')
  }

  return (
    <SwiperPage navto={goDetail}>
      <div className="img-index imgcommon" />
    </SwiperPage>
  )
}

export default LandingPage
