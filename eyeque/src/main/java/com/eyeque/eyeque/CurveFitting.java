package com.eyeque.eyeque;

/**
 *
 * File:            CurveFitting.java
 * Description:     Algorithm to calculate the spherical, cylindrical and axis using
 *                  the curve fitting algorithm
 * Created:         2016/06/26
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class CurveFitting
{
    /**
     * Curve fitting double [ ].
     *
     * @param angl the angl
     * @param P    the p
     * @param nm   the nm
     * @return the double [ ]
     */
    public double[] curveFitting(double[]  angl, double[]  P, int nm)
    {
        double M_PI=Math.PI;
        
        double coss2=0,coss=0,cossp=0,sph=0,cyl=0,sph2=0,cyl2=0;
        double   kkk=1000;
        double   kk=0;
        int   axis=0;
        double sum=0;
        for(int ii=0;ii<nm;ii++)
        {
            sum+=P[ii];
        }
        
        for(float i=0;i<180;i++)
        {
            kk=0;
            coss2=0;
            coss=0;
            cossp=0;
            for(int ii=0;ii<nm;ii++)
            {
                coss+=Math.cos(2*(angl[ii]-i)/180.0*M_PI);
                coss2+=(Math.cos(2*(angl[ii]-i)/180.0*M_PI))*(Math.cos(2*(angl[ii]-i)/180.0*M_PI));
                cossp+=Math.cos(2*(angl[ii]-i)/180.0*M_PI)*P[ii];
            }
            
            double dNM=(double) nm;
            cyl=(cossp-coss*sum/dNM)/(coss2-coss*coss/dNM);
            sph=sum/nm-cyl*coss/dNM+cyl;
            cyl=-2*cyl;
            if(cyl<=0)
            {
                for(int ii=0;ii<nm;ii++)
                    kk+=(sph+cyl*Math.sin((i-angl[ii])/180.0*M_PI)*Math.sin((i-angl[ii])/180.0*M_PI)-P[ii])*(sph+cyl*Math.sin((i-angl[ii])/180.0*M_PI)*Math.sin((i-angl[ii])/180.0*M_PI)-P[ii]);
                if (kk<=kkk)
                {
                    kkk=kk;
                    sph2=sph;
                    cyl2=cyl;
                    axis=(int)i;
                }
            }
        }
        
        double [] retDouble = new double[3];
        
        retDouble[0]=sph;
        retDouble[1]=cyl;
        retDouble[2]=axis;
        
        return retDouble;
    }

    /**
     * Alternative curve fitting function
     *
     * Curve fitting 1 double [ ].
     *
     * @param x the x
     * @param y the y
     * @return the double [ ]
     */
    public double[] curveFitting1(double []  x, double []  y)
    {
        int N = x.length;
        double kkk=0;
        double kk=0;
        int   axis=0;
        
        double coss2=0,coss=0,cossp=0,sph=0,cyl=0;
        
        double sum=0;
        for(int i=0;i<N;i++)
        {
            sum+=y[i];
        }
        
        
        for(int i=0;i<180;i++)
        {
            kk=0;
            for(int ii=0;ii<N;ii++)
            {
                kk+=(y[ii]-sum/N)*Math.cos(2*(x[ii]-i)/180*Math.PI);
            }
            
            if (kk>=kkk)
            {
                kkk=kk;
                axis=i;
            }
        }
        
        for(int ii=0;ii<N;ii++)
        {
            coss+=Math.cos(2*(x[ii]-axis)/180*Math.PI);
            coss2+=(Math.cos(2*(x[ii]-axis)/180*Math.PI))*(Math.cos(2*(x[ii]-axis)/180*Math.PI));
            cossp+=Math.cos(2*(x[ii]-axis)/180*Math.PI)*y[ii];
        }
        
        cyl=(cossp-coss*sum/N)/(coss2-coss*coss/N);
        sph=sum/N-cyl*coss/N+cyl;
        cyl=-2*cyl;
        
        double [] retDouble = new double[3];
        
        retDouble[0]=sph;
        retDouble[1]=cyl;
        retDouble[2]=axis;
        
        return retDouble;
    }
}